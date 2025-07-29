package rising.bot.preset

object AuctionPreset {
    val 상단일 = listOf(
        NamePreset("추피상",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 260, 260)
            )
        ),
        NamePreset("적주피 상",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 42, 200, 200)
            )
        ),
        NamePreset("낙인력 상",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 44, 800, 800)
            )
        ),
        NamePreset("공격력 상",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 155, 155)
            )
        ),
        NamePreset("무공 상",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 46, 300, 300)
            )
        ),
        NamePreset("치적 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 49, 155, 155)
            )
        ),
        NamePreset("치피 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 400, 400)
            )
        ),
        NamePreset("아공강 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 500, 500)
            )
        ),
        NamePreset("아피강 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 52, 750, 750)
            )
        ),
    )

    val 중중 = listOf(
//        추피 중, 적주피 중
        NamePreset("추피 중 적주피 중",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 260, 160),
                AuctionEtcOption(7, 42, 120, 120)
            )
        ),
//        낙인력 중, 세레나데 중
        NamePreset("낙인력 상 세레나데 중",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 44, 800, 480),
                AuctionEtcOption(7, 43, 360, 360)
            )
        ),
//        공격력 중, 무공 중
        NamePreset("공격력 중 무공 중",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 155, 95),
                AuctionEtcOption(7, 46, 180, 180)
            )
        ),
//        치피 중, 치적 중
        NamePreset("치피 중 치적 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 400, 240),
                AuctionEtcOption(7, 49, 95, 95)
            )
        ),
//        아피강 중, 아공강 중
        NamePreset("아피강 중 아공강 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 300, 300),
                AuctionEtcOption(7, 52, 750, 450)
            )
        ),
    )

    val 상하 = listOf(
        NamePreset("추피 상 적주피 하",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 260, 260),
                AuctionEtcOption(7, 42, 55, 55)
            )
        ),
        NamePreset("적주피 상 추피 하",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 70, 70),
                AuctionEtcOption(7, 42, 200, 200)
            )
        ),
        NamePreset("낙인력 상 세레나데 하",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 44, 800, 800),
                AuctionEtcOption(7, 43, 160, 160)
            )
        ),
        NamePreset("공격력 상 무공 하",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 155, 155),
                AuctionEtcOption(7, 46, 80, 80)
            )
        ),
        NamePreset("무공 상 공격력 하",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 40, 40),
                AuctionEtcOption(7, 46, 300, 300)
            )
        ),
        NamePreset("치피 상 치적 하",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 400, 400),
                AuctionEtcOption(7, 49, 40, 40)
            )
        ),
        NamePreset("치피 상 치적 하",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 110, 110),
                AuctionEtcOption(7, 49, 155, 155)
            )
        ),
        NamePreset("아공강 상 아피강 하",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 500, 500),
                AuctionEtcOption(7, 52, 200, 200)
            )
        ),
        NamePreset("아피강 상 아공강 하",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 135, 135),
                AuctionEtcOption(7, 52, 750, 750)
            )
        ),
    )
    val 상중 = listOf(
        NamePreset("추피 상 적주피 중",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 260, 260),
                AuctionEtcOption(7, 42, 120, 120)
            )
        ),
        NamePreset("적주피 상 추피 중",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 160, 160),
                AuctionEtcOption(7, 42, 200, 200)
            )
        ),
//        낙인력 상, 세레나데 중
        NamePreset("낙인력 상 세레나데 중",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 44, 800, 800),
                AuctionEtcOption(7, 43, 360, 360)
            )
        ),
//        공격력 상, 무공 중
        NamePreset("공격력 상 무공 중",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 155, 155),
                AuctionEtcOption(7, 46, 180, 180)
            )
        ),
//        무공 상, 공격력 중
        NamePreset("무공 상 공격력 중",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 95, 95),
                AuctionEtcOption(7, 46, 300, 300)
            )
        ),
//        치피 상, 치적 중
        NamePreset("치피 상 치적 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 400, 400),
                AuctionEtcOption(7, 49, 95, 95)
            )
        ),
        NamePreset("치적 상 치피 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 240, 240),
                AuctionEtcOption(7, 49, 155, 155)
            )
        ),
        NamePreset("아피강 상 아공강 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 300, 300),
                AuctionEtcOption(7, 52, 750, 750)
            )
        ),
        NamePreset("아공강 상 아피강 중",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 500, 500),
                AuctionEtcOption(7, 52, 450, 450)
            )
        ),
    )
    val 상상 = listOf(
//        추피 상, 적주피 상
        NamePreset("적주피 상 추피 상",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 41, 260, 260),
                AuctionEtcOption(7, 42, 200, 200)
            )
        ),
        NamePreset("낙인력 상 세레나데 상",
            categoryCode = 200010,
            listOf(
                AuctionEtcOption(7, 44, 800, 800),
                AuctionEtcOption(7, 43, 600, 600)
            )
        ),
        NamePreset("공격력 상 무공 상",
            categoryCode = 200020,
            listOf(
                AuctionEtcOption(7, 45, 155, 155),
                AuctionEtcOption(7, 46, 300, 300)
            )
        ),
        NamePreset("치피 상 치적 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 50, 400, 400),
                AuctionEtcOption(7, 49, 155, 155)
            )
        ),
        NamePreset("아피강 상 아공강 상",
            categoryCode = 200030,
            listOf(
                AuctionEtcOption(7, 51, 500, 500),
                AuctionEtcOption(7, 52, 750, 750)
            )
        ),
    )
}

data class NamePreset(
    val name: String,
    val categoryCode: Int,
    val ectOptions: List<AuctionEtcOption>
)

data class AuctionEtcOption(
    val firstOption: Int,
    val secondOption: Int?,
    val minValue: Int,
    val maxValue: Int
)