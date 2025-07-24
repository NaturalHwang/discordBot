package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MarketItemRequest(
    @JsonProperty("Sort") var sort:String,
    @JsonProperty("CategoryCode") var categoryCode: Int,
    @JsonProperty("ItemGrade") var itemGrade: String,
    @JsonProperty("PageNo") var pageNo: Int,
    @JsonProperty("SortCondition") var sortCondition: String,
    @JsonProperty("ItemName") var itemName: String?
)