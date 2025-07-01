package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CharacterDetailWrapper(
    @JsonProperty("ArmoryProfile")
    val armoryProfile: CharacterDetailDto? = null
)

data class CharacterDetailDto(
    @JsonProperty("CharacterName")
    val characterName: String? = null,
    @JsonProperty("CharacterLevel")
    val characterLevel: Int? = null,
    @JsonProperty("ItemAvgLevel")
    val itemAvgLevel: String? = null,
    @JsonProperty("ItemMaxLevel")
    val itemMaxLevel: String? = null,
    @JsonProperty("ExpeditionLevel")
    val expeditionLevel: Int? = null,
    @JsonProperty("CharacterClassName")
    val characterClassName: String? = null,
    @JsonProperty("ServerName")
    val serverName: String? = null,
    @JsonProperty("GuildName")
    val guildName: String? = null,
    @JsonProperty("PvpGradeName")
    val pvpGradeName: String? = null,
    @JsonProperty("TownLevel")
    val townLevel: Int? = null,
    @JsonProperty("TownName")
    val townName: String? = null,
    @JsonProperty("Title")
    val title: String? = null,
    @JsonProperty("CharacterImage")
    val characterImage: String? = null
    // 필요시 추가 필드 선언
)