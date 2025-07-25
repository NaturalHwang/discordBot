package rising.bot.service

import org.springframework.stereotype.Service

@Service
class bunbaeService {
    fun bunbae(gold: Long, people: Int): String {
        // 판매 실수령액 (수수료 5%)
        val sellAfterFee = gold * 0.95

        // 경매 분배금: 10만 초과분만 5% 수수료 적용 (시세가 경매 낙찰가와 같다는 가정)
//        val auctionDistributedTotal = if (gold <= 100_000) {
//            gold.toDouble()
//        } else {
//            100_000.0 + (gold - 100_000) * 0.95
//        }
//        val auctionDistribute = auctionDistributedTotal / (people - 1)

        // https://lostark.game.onstove.com/News/Notice/Views/13173 패치 노트 적용

        // 손익분기점: 판매 실수령액
        val breakeven = (sellAfterFee / (1 + 0.95 / (people - 1))).toLong()
        // 추천 입찰가: 손익분기점의 90% 또는 1.1로 나눈 값
        val recommand = (breakeven * 0.9).toLong()

        val auctionDistribute = ((breakeven.toDouble() / (people - 1)) * 0.95).toLong()

        return """
            시세(판매가): $gold
            인원: $people
            
            판매 실수령액: ${sellAfterFee.toLong()}
            추천 입찰가: $recommand ~ $breakeven 사이
            손익 분기점(본전 낙찰가): $breakeven
            경매 분배금: ${auctionDistribute}
        """.trimIndent()
    }
}

// 낙찰자 수익 = 낙찰자의 판매 금액 * 0.95 - 낙찰금액
// 참여자 수익 = 낙찰금액 / (참여인원 - 1) * 0.95
// 손익분기점 조건: 낙찰자의 판매 금액 * 0.95 - 낙찰금액 = 낙찰금액 / (참여인원 -1) * 0.95
// 낙찰금액 = 판매금액 * 0.95 / {1 + (0.95 / n-1)}

//시세(판매가): 240000
//인원: 8
//
//경매 분배금: 33285
//판매 실수령액: 228000
//추천 입찰가: 177012
//손익 분기점(본전 낙찰가): 194714