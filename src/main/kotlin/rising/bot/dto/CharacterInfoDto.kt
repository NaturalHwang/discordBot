package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CharacterInfoDto(
    @JsonProperty("ServerName")
    val serverName: String,
    @JsonProperty("CharacterName")
    val characterName: String,
    @JsonProperty("CharacterLevel")
    val characterLevel: Int,
    @JsonProperty("CharacterClassName")
    val characterClassName: String,
    @JsonProperty("ItemAvgLevel")
    val itemAvgLevel: String,
)
