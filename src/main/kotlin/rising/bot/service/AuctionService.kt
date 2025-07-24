package rising.bot.service

import org.springframework.stereotype.Service
import rising.bot.component.LoaApiClient
import rising.bot.dto.AuctionItemSearchRequest
import rising.bot.dto.MarketItemRequest
import rising.bot.preset.AuctionPreset
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

    fun detailAuctionSearch(quality:Int?, option1: String?, option2: String?): String {
        val allPresets = AuctionPreset.상단일 + AuctionPreset.상하 + AuctionPreset.상중 + AuctionPreset.상상

        val normOpt1 = option1?.replace(" ", "")
        val normOpt2 = option2?.replace(" ", "")

        val matchedPreset = allPresets.find { preset ->
            val normName = preset.name.replace(" ", "")
            if(normOpt2.isNullOrBlank()) {
                normName.contains(normOpt1!!)
            } else {
                normName.contains(normOpt1!!) && normName.contains(normOpt2)
            }
        }

        val request = baseRequest.copy(
            categoryCode = (matchedPreset?.categoryCode)!!,
            etcOptions = (matchedPreset?.ectOptions)!!,
            itemGradeQuality = quality
        )

        val result = loaApi.searchAuctionItems(request)

        if (result?.items == null || result.items.isEmpty()) {
            return "검색 결과가 존재하지 않습니다."
        }
//        // 가독성 떨어져서 아래 코드로 변경
//        return result!!.items!!.joinToString ("\n") { item ->
//            val statMap = item.options
//                .filter { it.type == 5 }
//                .associateBy({ it.optionName }, { it.value })
//
//            val 힘민지 = statMap["지능"] ?: "-"
//
//            "${item.name} 거래 횟수: ${item.auctionInfo.tradeAllowCount} 품질: ${item.gradeQuality} 힘민지 $힘민지 즉시 구매가: ${item.auctionInfo.buyPrice}"
//        }
        return result!!.items!!
            .filter { it.gradeQuality >= (quality ?: 0) }
            .take(5)
            .joinToString("\n-----------------------------\n") { item ->
                val statMap = item.options.filter { it.type == 5 }
                    .associateBy({ it.optionName }, { it.value })
                val 힘민지 = statMap["힘"] ?: "-"

                """
                [${item.name}]
                - 거래 횟수: ${item.auctionInfo.tradeAllowCount}
                - 품질: ${item.gradeQuality}
                - 힘민지: $힘민지
                - 즉시 구매가: ${item.auctionInfo.buyPrice}
                """.trimIndent()
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