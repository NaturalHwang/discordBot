package rising.bot.service

import org.springframework.stereotype.Service
import rising.bot.component.LoaApiClient
import rising.bot.dto.AuctionItemSearchRequest
import rising.bot.dto.MarketItemRequest
import rising.bot.preset.NamePreset

@Service
class AuctionService (
    private val loaApi: LoaApiClient
){
    val baseRequest = AuctionItemSearchRequest(
        categoryCode = 0,
        sortCondition = "ASC",
        sort = "BUY_PRICE",
        itemGrade = "고대",
        itemTier = 4,
        itemGradeQuality = null,
        itemLevelMin = 0,
        itemLevelMax = 0,
        pageNo = 1,
        characterClass = "",
        itemName = "",
        etcOptions = emptyList(),
        skillOptions = emptyList()
    )

    fun handleAuctionPreset(
        presets: List<NamePreset>,
    ): String {
        val categoryNameMap = mapOf(
            200010 to "목걸이",
            200020 to "귀걸이",
            200030 to "반지"
        )
        // 프리셋 처리 결과를 카테고리 코드별로 그룹화
        val groupedResults = presets
            .map { namedPreset ->
                val request = baseRequest.copy(
                    categoryCode = namedPreset.categoryCode,
                    etcOptions = namedPreset.ectOptions
                )
                val result = loaApi.searchAuctionItems(request)
                val minPrice = result?.items?.orEmpty()!!
                    .mapNotNull { it.auctionInfo.buyPrice }
                    .minOrNull()
                val msg = if (minPrice != null) {
                    "${namedPreset.name}: $minPrice"
                } else {
                    "${namedPreset.name}: -"
                }
                // Pair(카테고리코드, 결과문자열)
                namedPreset.categoryCode to msg
            }
            .groupBy { it.first }  // 카테고리 코드별로 그룹화

        // 각 카테고리별로 결과 묶어서 출력
        return groupedResults.entries.joinToString("\n\n") { (categoryCode, items) ->
            val header = categoryNameMap[categoryCode] ?: "기타"
            val body = items.joinToString("\n") { it.second }
            "$header\n$body"
        }
    }

    val gemNameMap = mapOf(
        "겁" to "겁화의 보석",
        "멸" to "멸화의 보석",
        "작" to "작열의 보석",
        "홍" to "홍염의 보석"
    )

    fun parseGemInput(input: String): String? {
        val regex = Regex("""(\d+)(겁|멸|작|홍)""")
        val match = regex.matchEntire(input.trim())
        if (match != null) {
            val level = match.groupValues[1]
            val type = match.groupValues[2]
            val gemName = gemNameMap[type] ?: return null
            return "${level}레벨 $gemName"
        }
        return null
    }

    fun makeGemSearchRequest(itemName: String): AuctionItemSearchRequest {
        return AuctionItemSearchRequest(
            categoryCode = 210000,
            sortCondition = "ASC",
            sort = "BUY_PRICE",
            itemGrade = null,
            itemTier = null,
            itemGradeQuality = null,
            itemLevelMin = 0,
            itemLevelMax = 0,
            pageNo = 1,
            characterClass = "",
            itemName = itemName,
            etcOptions = emptyList(),
            skillOptions = emptyList()
        )
    }

    fun findGemMinPrice(input: String): String {
        val itemName = parseGemInput(input)
        if (itemName == null) {
            return "올바른 형식(예: 7겁, 8작, 9멸, 10홍 등)으로 입력해 주세요."
        }
        val request = makeGemSearchRequest(itemName)
        val response = loaApi.searchAuctionItems(request)
        val minPrice = response?.items?.orEmpty()?.mapNotNull { it.auctionInfo.buyPrice }?.minOrNull()
        return if (minPrice != null) {
            "$itemName 최저가: $minPrice 골드"
        } else {
            "$itemName 결과 없음"
        }
    }

    val marketRequest = MarketItemRequest(
        sort = "Current_Min_Price",
        categoryCode = 40000,
        itemGrade = "유물",
        pageNo = 0,
        sortCondition = "DESC",
        itemName = null
    )

    fun getExpensiveEngravingBooks(): String {
        val request = marketRequest
        val response = loaApi.searchMarketItems(request)

        return response!!.items.joinToString(separator = "\n") { item ->
            "${item.name}: ${item.currentMinPrice}"
        }
    }
}