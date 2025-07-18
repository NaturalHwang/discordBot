package rising.bot.component

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import rising.bot.dto.*

@Component
class LoaApiClient(
    private val restTemplate: RestTemplate,
    private val loaProperties: LoaProperties
) {
    private val apiBase = "https://developer-lostark.game.onstove.com/"

    private val tokenCooldowns = mutableMapOf<String, Long>()

    private fun <T> swapTokens(apiCall: (String) -> T): T {
        var lastException: Exception? = null
        for(token in loaProperties.tokens) {
            val cooldownUntil = tokenCooldowns[token] ?: 0L
            val now = System.currentTimeMillis()
            if (now < cooldownUntil) continue // 쿨타임 중이면 사용x
            try {
                return apiCall(token)
            } catch (e: org.springframework.web.client.HttpClientErrorException.TooManyRequests) {
                lastException = e
                tokenCooldowns[token] = now + 60_000 // 1분 쿨타임
            }
        }
        throw lastException ?: RuntimeException("API 호출 실패 - 모든 토큰 소진")
    }

    fun siblings(mainName: String): List<CharacterInfoDto> {
        return swapTokens{ token ->
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

    fun detail(name: String): CharacterDetailDto? {
        return swapTokens { token ->
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
        return swapTokens { token ->
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
            response.body?.toList() ?: emptyList()
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