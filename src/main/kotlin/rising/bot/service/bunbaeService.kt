package rising.bot.service

import org.springframework.stereotype.Service

@Service
class bunbaeService {
    fun bunbae(gold: Long, people: Int): String {
        // 판매 실수령액 (수수료 5%)
        val sellAfterFee = gold * 0.95

        // 경매 분배금: 10만 초과분만 5% 수수료 적용 (시세가 경매 낙찰가와 같다는 가정)
        val auctionDistributedTotal = if (gold <= 100_000) {
            gold.toDouble()
        } else {
            100_000.0 + (gold - 100_000) * 0.95
        }
        val auctionDistribute = auctionDistributedTotal / (people - 1)

        // 손익분기점: 판매 실수령액
        val breakeven = (sellAfterFee - auctionDistribute).toLong()
        // 추천 입찰가: 손익분기점의 90% 또는 1.1로 나눈 값
        val recommand = (breakeven / 1.1).toLong()

        return """
            시세(판매가): $gold
            인원: $people
            
            경매 분배금: ${auctionDistribute.toLong()}
            판매 실수령액: ${sellAfterFee.toLong()}
            추천 입찰가: $recommand ~ $breakeven 사이
            손익 분기점(본전 낙찰가): $breakeven
        """.trimIndent()
    }
}

//시세(판매가): 240000
//인원: 8
//
//경매 분배금: 33285
//판매 실수령액: 228000
//추천 입찰가: 177012
//손익 분기점(본전 낙찰가): 194714