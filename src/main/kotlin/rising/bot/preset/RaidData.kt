package rising.bot.preset

object RaidData {
    val info = listOf(
        RaidPreset(DifficultyLevel.NORMAL,"서막", 1,62802745968),
        RaidPreset(DifficultyLevel.NORMAL,"서막", 2,80672317989),
        RaidPreset(DifficultyLevel.NORMAL,"1막", 1,161517294610),
        RaidPreset(DifficultyLevel.NORMAL,"1막", 2,213231745024),
        RaidPreset(DifficultyLevel.NORMAL,"2막", 1,275449621248),
        RaidPreset(DifficultyLevel.NORMAL,"2막", 2,399401958090),
        RaidPreset(DifficultyLevel.NORMAL,"3막", 1,368773967531),
        RaidPreset(DifficultyLevel.NORMAL,"3막", 2,334691604286),
        RaidPreset(DifficultyLevel.NORMAL,"3막", 3,731975350664),
        RaidPreset(DifficultyLevel.NORMAL,"익카", 1,769752730817),

        RaidPreset(DifficultyLevel.HARD,"서막", 1,108972915945),
        RaidPreset(DifficultyLevel.HARD,"서막", 2,154366968702),
//        팔 100억, 실드 200억
        RaidPreset(DifficultyLevel.HARD,"1막", 1,299024428126),
//        히든 에아달린 257억
        RaidPreset(DifficultyLevel.HARD,"1막", 2,372905572766),
//        배리어 500억, 500억
        RaidPreset(DifficultyLevel.HARD,"2막", 1,598432060783),
//        니나브/+1,히든 아제나 -103억, -171억, -660억
        RaidPreset(DifficultyLevel.HARD,"2막", 2,817760408772),
        RaidPreset(DifficultyLevel.HARD,"3막", 1,622059653375),
        RaidPreset(DifficultyLevel.HARD,"3막", 2,663116555628),
        RaidPreset(DifficultyLevel.HARD,"3막", 3,1473779836172),
//        아제나 228.5억 공카 431억 대격 카단 95억
        RaidPreset(DifficultyLevel.HARD,"익카", 1,1859003510628)
    )
}

enum class DifficultyLevel(val korean: String) {
    NORMAL("노말"),
    HARD("하드"),
    INFERNO("헬"),
    THEFIRST("더퍼");

    companion object {
        fun fromKorean(korean: String): DifficultyLevel? {
            return values().find { it.korean == korean }
        }
    }
}

data class RaidPreset(
    val level: DifficultyLevel, // 난이도: Normal, Hard, etc.
    val raidAct: String,        // Act 1, 2, 3
    val gate: Int,              // 관문 번호
    val hp: Long                // 해당 관문 보스 체력
)