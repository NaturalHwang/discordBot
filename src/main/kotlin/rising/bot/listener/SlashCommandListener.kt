package rising.bot.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.service.GoldScheduleService

@Component
class SlashCommandListener(
    private val goldScheduleService: GoldScheduleService
) : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        when (event.name) {
            "쌀섬" -> {
                val result = goldScheduleService.findGoldSchedule()
                event.reply(result).queue()
            }
//            명령어 추가 부분
            else -> {
                event.reply("알 수 없는 명령어입니다.").setEphemeral(true).queue()
            }
        }
    }
}