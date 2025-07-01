package rising.bot.loa

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
    private val apiBase = "https://developer-lostark.game.onstove.com"

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
}
