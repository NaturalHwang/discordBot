package rising.bot.command.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.preset.DifficultyLevel
import rising.bot.service.RaidInfoService

@Component
class RaidSlashCommandListener(
    private val raid: RaidInfoService
) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "damagerate" -> {
                val levelKor = event.getOption("난이도")?.asString ?: return
                val act = event.getOption("레이드")?.asString ?: return
                val gate = event.getOption("관문")?.asInt ?: return
                val myDamage = event.getOption("딜량")?.asDouble?.toLong() ?: return

                val level = DifficultyLevel.fromKorean(levelKor)
                if (level == null) {
                    event.reply("❌ 올바르지 않은 난이도입니다: `$levelKor`")
                        .setEphemeral(true)
                        .queue()
                    return
                }

                event.deferReply(true).queue { hook ->
                    val resultMessage = raid.myDamagePercent(level, act, gate, myDamage)
                    if (resultMessage != null) {
                        hook.editOriginal("✅ $resultMessage").queue()
                    } else {
                        hook.editOriginal("❌ `${level.korean} ${act} ${gate}관문` 정보가 존재하지 않습니다.")
                            .queue()
                    }
                }
            }
        }
    }
}
