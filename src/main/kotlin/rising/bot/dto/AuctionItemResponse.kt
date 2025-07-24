package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AuctionItemResponse(
    @JsonProperty("PageNo") val pageNo: Int,
    @JsonProperty("PageSize") val pageSize: Int,
    @JsonProperty("TotalCount") val totalCount: Int,
    @JsonProperty("Items") val items: List<AuctionItem>? = emptyList()
)

data class AuctionItem(
    @JsonProperty("Name") val name: String,
    @JsonProperty("Grade") val grade: String,
    @JsonProperty("Tier") val tier: Int,
    @JsonProperty("Level") val level: Int,
    @JsonProperty("Icon") val icon: String,
    @JsonProperty("GradeQuality") val gradeQuality: Int,
    @JsonProperty("AuctionInfo") val auctionInfo: AuctionInfo,
    @JsonProperty("Options") val options: List<AuctionOption>
)

data class AuctionInfo(
    @JsonProperty("StartPrice") val startPrice: Int,
    @JsonProperty("BuyPrice") val buyPrice: Int?,
    @JsonProperty("BidPrice") val bidPrice: Int?,
    @JsonProperty("EndDate") val endDate: String,
    @JsonProperty("BidCount") val bidCount: Int,
    @JsonProperty("BidStartPrice") val bidStartPrice: Int,
    @JsonProperty("IsCompetitive") val isCompetitive: Boolean,
    @JsonProperty("TradeAllowCount") val tradeAllowCount: Int,
    @JsonProperty("UpgradeLevel") val upgradeLevel: Int
)

data class AuctionOption(
    @JsonProperty("Type") val type: Any?,
    @JsonProperty("OptionName") val optionName: String,
    @JsonProperty("OptionNameTripod") val optionNameTripod: String?,
    @JsonProperty("Value") val value: Any?,
    @JsonProperty("IsPenalty") val isPenalty: Boolean,
    @JsonProperty("ClassName") val className: String?,
    @JsonProperty("IsValuePercentage") val isValuePercentage: Boolean?
)