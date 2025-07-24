package rising.bot.component

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.firebase.database.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import rising.bot.dto.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class LoaApiClient(
    private val restTemplate: RestTemplate,
    private val firebaseDatabase: FirebaseDatabase,
//    private val loaProperties: LoaProperties,
    @Value("\${aes.secret.key}")
    private val aesSecretKey: String,
    @Value("\${loa.token}")
    private val token: String,
) {
    private val apiBase = "https://developer-lostark.game.onstove.com/"

    private val tokenCooldowns = mutableMapOf<String, Long>()

    // 1️⃣ API 키 암호화/복호화 도우미
    fun encryptApiKey(apiKey: String): String {
        val key = SecretKeySpec(aesSecretKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return Base64.getEncoder().encodeToString(cipher.doFinal(apiKey.toByteArray()))
    }

    fun decryptApiKey(encryptedApiKey: String): String {
        val key = SecretKeySpec(aesSecretKey.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(Base64.getDecoder().decode(encryptedApiKey)))
    }

    // API 키 저장 (등록)
    fun registerApiKey(
        guildId: String, channelId: String, apiKey: String, onComplete: (Boolean) -> Unit
    ) {
        getApiKeys(guildId, channelId) { existingKeys ->
            if (existingKeys.any { it.trim() == apiKey.trim() }) {
                // 이미 등록된 키
                onComplete(false)
                return@getApiKeys
            }
            // 중복이 아니면 등록 진행
            val encryptedKey = encryptApiKey(apiKey)
            firebaseDatabase.getReference("channels")
                .child(guildId)
                .child(channelId)
                .child("apiKeys")
                .push()
                .setValue(encryptedKey, DatabaseReference.CompletionListener { error, _ ->
                    onComplete(error == null)
                })
        }
    }

    // API 키 조회 (비동기)
    fun getApiKeys(guildId: String, channelId: String, onResult: (List<String>) -> Unit) {
        firebaseDatabase.getReference("channels")
            .child(guildId)
            .child(channelId)
            .child("apiKeys")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val encryptedList = snapshot.children.mapNotNull { it.getValue(String::class.java) }
                    val decrypted = encryptedList.map { decryptApiKey(it) }
                    onResult(decrypted)
                }
                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

//    private fun <T> swapTokens(apiCall: (String) -> T): T {
//        var lastException: Exception? = null
//        for(token in loaProperties.tokens) {
//            val cooldownUntil = tokenCooldowns[token] ?: 0L
//            val now = System.currentTimeMillis()
//            if (now < cooldownUntil) continue // 쿨타임 중이면 사용x
//            try {
//                return apiCall(token)
//            } catch (e: org.springframework.web.client.HttpClientErrorException.TooManyRequests) {
//                lastException = e
//                tokenCooldowns[token] = now + 60_000 // 1분 쿨타임
//            }
//        }
//        throw lastException ?: RuntimeException("API 호출 실패 - 모든 토큰 소진")
//    }

    private fun <T> swapApiKeys(apiKeys: List<String>, apiCall: (String) -> T): T {
        var lastException: Exception? = null
        for (token in apiKeys) {
            val cooldownUntil = tokenCooldowns[token] ?: 0L
            val now = System.currentTimeMillis()
            if (now < cooldownUntil) continue // 쿨타임 중이면 스킵
            try {
                return apiCall(token)
            } catch (e: org.springframework.web.client.HttpClientErrorException.TooManyRequests) {
                lastException = e
                tokenCooldowns[token] = now + 60_000 // 1분 쿨타임
            }
        }
        throw lastException ?: RuntimeException("API 호출 실패 - 모든 토큰 소진")
    }

    fun siblings(mainName: String, apiKeys: List<String>): List<CharacterInfoDto> {
        return swapApiKeys(apiKeys){ token ->
            val url = UriComponentsBuilder.fromHttpUrl("$apiBase/characters/{name}/siblings")
                .buildAndExpand(mainName)
                .toUriString()

            val headers = HttpHeaders().apply {
                set("Authorization", "Bearer $token")
            }
            val entity = HttpEntity<Void>(headers)

            val response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                Array<CharacterInfoDto>::class.java
            )
            response.body?.toList() ?: emptyList()
        }
    }

    fun detail(name: String, apiKeys: List<String>): CharacterDetailDto? {
        return swapApiKeys(apiKeys) { token ->
            val url = UriComponentsBuilder.fromHttpUrl("$apiBase/armories/characters/{name}")
                .buildAndExpand(name)
                .toUriString()

            val headers = HttpHeaders().apply {
                set("Authorization", "Bearer $token")
            }
            val entity = HttpEntity<Void>(headers)

            val response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                entity,
                CharacterDetailWrapper::class.java
            )
            response.body?.armoryProfile
        }
    }

    fun calendar(): List<ContentsCalendar> {
        val url = "$apiBase/gamecontents/calendar"
        val headers = HttpHeaders().apply {
            set("Authorization", "Bearer $token")
        }
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(
            url,
            org.springframework.http.HttpMethod.GET,
            entity,
            Array<ContentsCalendar>::class.java
        )
        return response.body?.toList() ?: emptyList()
    }

    fun searchAuctionItems(request: AuctionItemSearchRequest, apiKeys: List<String>): AuctionItemResponse? {
        return swapApiKeys(apiKeys) { token ->
            val url = "$apiBase/auctions/items"
            val headers = HttpHeaders().apply {
                set("Authorization", "Bearer $token")
                set("accept", "application/json")
                contentType = org.springframework.http.MediaType.APPLICATION_JSON
            }
            val entity = HttpEntity(request, headers)
            val response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.POST,
                entity,
                AuctionItemResponse::class.java
            )
            response.body
        }
    }

    fun searchMarketItems(request: MarketItemRequest, apiKeys: List<String>): MarketItemResponse? {
        return swapApiKeys(apiKeys) { token ->
            val url = "$apiBase/markets/items"
            val headers = HttpHeaders().apply {
                set("Authorization", "Bearer $token")
                set("accept", "application/json")
                contentType = org.springframework.http.MediaType.APPLICATION_JSON
            }
            val entity = HttpEntity(request, headers)
            val response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.POST,
                entity,
                MarketItemResponse::class.java
            )
            response.body
        }
    }
}

data class ContentsCalendar(
    @JsonProperty("CategoryName")
    val categoryName: String,
    @JsonProperty("ContentsName")
    val contentsName: String,
    @JsonProperty("ContentsIcon")
    val contentsIcon: String,
    @JsonProperty("MinItemLevel")
    val minItemLevel: Int,
    @JsonProperty("StartTimes")
    val startTimes: List<String>?,
    @JsonProperty("Location")
    val location: String,
    @JsonProperty("RewardItems")
    val rewardItems: List<RewardItem>
)

data class RewardItem(
    @JsonProperty("ItemLevel")
    val itemLevel: Int,
    @JsonProperty("Items")
    val items: List<Item>
)

data class Item(
    @JsonProperty("Name")
    val name: String,
    @JsonProperty("Icon")
    val icon: String,
    @JsonProperty("Grade")
    val grade: String,
    @JsonProperty("StartTimes")
    val startTimes: List<String>?
)