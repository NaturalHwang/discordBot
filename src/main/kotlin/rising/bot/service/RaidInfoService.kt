package rising.bot.service

import org.springframework.stereotype.Service
import rising.bot.preset.DifficultyLevel
import rising.bot.preset.RaidData
import java.text.NumberFormat
import java.util.*

@Service
class RaidInfoService {
    fun myDamagePercent(
        level: DifficultyLevel,
        act: String,
        gate: Int,
        myDamage: Long
    ): String? {
        val matchedPreset = RaidData.info.find { preset ->
            preset.level == level &&
                    preset.raidAct.replace(" ", "") == act.replace(" ", "") &&
                    preset.gate == gate
        } ?: return null

        val myRate = myDamage.toDouble() / matchedPreset.hp.toDouble() * 100
        val formattedPercent = String.format("%.1f", myRate)

        val label = when {
            myRate < 15 -> "🟢 투사"
            myRate < 20 -> "🔵 강투"
            myRate < 25 -> "🟡 잔혈"
            else -> "🔴 캐리머신 👊"
        }

        val nf = NumberFormat.getInstance(Locale.KOREA)
        val bossHp = nf.format(matchedPreset.hp)
        val myDmg = nf.format(myDamage)

        return """
             **딜 지분 계산 결과**
            ─────────────
            🔹 난이도: ${level.korean}
            🔹 레이드: $act ${gate}관문
            🔹 보스 체력: $bossHp
            🔹 당신의 딜량: $myDmg
            🔹 **기여도: $formattedPercent%%** ($label)
            ─────────────
            ※ 에스더 택틱, 오브젝트 딜량은 반영되지 않을 수 있습니다.
        """.trimIndent()
    }
}
