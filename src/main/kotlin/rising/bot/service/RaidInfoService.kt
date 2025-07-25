package rising.bot.service

import org.springframework.stereotype.Service
import rising.bot.preset.DifficultyLevel
import rising.bot.preset.RaidData

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

        val myRate: Double = (myDamage.toDouble() / matchedPreset.hp) * 100
        val formattedRate = String.format("%.1f", myRate)

        val rateValue = formattedRate.toDouble()
        val label = when {
            rateValue < 15 -> "투사"
            rateValue < 20 -> "강투"
            rateValue < 25 -> "잔혈"
            else -> "캐리머신👊"
        }

        return "${level.korean} ${act} ${gate}관문 딜지분: $formattedRate% ($label)"
    }
}