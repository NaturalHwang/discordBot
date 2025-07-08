package rising.bot.loa

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import rising.bot.dto.CharacterDetailDto
import rising.bot.dto.CharacterDetailWrapper
import rising.bot.dto.CharacterInfoDto

@Component
class LoaApiClient(
    @Value("\${loa.token}") private val token: String,
    private val restTemplate: RestTemplate
) {
    private val apiBase = "https://developer-lostark.game.onstove.com/"

    fun siblings(mainName: String): List<CharacterInfoDto> {
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
        return response.body?.toList() ?: emptyList()
    }

    fun detail(name: String): CharacterDetailDto? {
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
        println(response.body)
        return response.body?.armoryProfile
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