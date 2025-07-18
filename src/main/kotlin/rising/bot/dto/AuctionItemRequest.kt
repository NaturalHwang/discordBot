package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty
import rising.bot.preset.AuctionEtcOption

data class AuctionItemSearchRequest(
    @JsonProperty("CategoryCode") var categoryCode: Int,
    @JsonProperty("SortCondition") var sortCondition: String,
    @JsonProperty("Sort") var sort: String,
    @JsonProperty("ItemGrade") var itemGrade: String?,
    @JsonProperty("ItemTier") var itemTier: Int?,
    @JsonProperty("ItemGradeQuality") var itemGradeQuality: Int?,
    @JsonProperty("ItemLevelMin") var itemLevelMin: Int,
    @JsonProperty("ItemLevelMax") var itemLevelMax: Int?,
    @JsonProperty("PageNo") var pageNo: Int,
    @JsonProperty("CharacterClass") var characterClass: String,
    @JsonProperty("ItemName") var itemName: String,
    @JsonProperty("EtcOptions") var etcOptions: List<AuctionEtcOption>,
    @JsonProperty("SkillOptions") var skillOptions: List<Any> = emptyList()
)