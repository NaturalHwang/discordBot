package rising.bot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MarketItemResponse(
    @JsonProperty("PageNo")
    val pageNo: Int,

    @JsonProperty("PageSize")
    val pageSize: Int,

    @JsonProperty("TotalCount")
    val totalCount: Int,

    @JsonProperty("Items")
    val items: List<Item>
) {
    data class Item(
        @JsonProperty("Id")
        val id: Long,
        @JsonProperty("Name")
        val name: String,
        @JsonProperty("Grade")
        val grade: String,
        @JsonProperty("Icon")
        val icon: String,
        @JsonProperty("BundleCount")
        val bundleCount: Int,
        @JsonProperty("TradeRemainCount")
        val tradeRemainCount: Int?,
//        전일 평균가
        @JsonProperty("YDayAvgPrice")
        val yDayAvgPrice: Long,
//        가장 최근 거래가
        @JsonProperty("RecentPrice")
        val recentPrice: Long,
//        현재 경매장 최저가
        @JsonProperty("CurrentMinPrice")
        val currentMinPrice: Long
    )
}