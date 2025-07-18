package rising.bot.service

import org.springframework.stereotype.Service
import rising.bot.component.LoaApiClient
import rising.bot.dto.AuctionItemSearchRequest
import rising.bot.preset.NamePreset

@Service
class AuctionService (
    private val loaApi: LoaApiClient
){
    fun handleAuctionPreset(
        presets: List<NamePreset>,
        baseRequest: AuctionItemSearchRequest
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
}