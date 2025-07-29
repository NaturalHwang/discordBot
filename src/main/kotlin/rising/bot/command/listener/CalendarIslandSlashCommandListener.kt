package rising.bot.command.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.service.GoldAlertService
import rising.bot.service.GoldCalendarScheduler
import java.util.concurrent.TimeUnit

@Component
class CalendarIslandSlashCommandListener(
    private val goldAlertService: GoldAlertService,
    private val goldCalendarScheduler: GoldCalendarScheduler,
) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id ?: return
        val channelId = event.channel.id

        when(event.name){
            "goldisland" -> {
                event.deferReply(true).queue()

                goldCalendarScheduler.findGoldSchedule { result ->
                    event.hook.editOriginal(result)
                        .delay(30, TimeUnit.SECONDS)
                        .flatMap { msg -> event.hook.deleteOriginal() }
                        .queue()
                }
            }

            "registeralert" -> {
                val member = event.member
                if (member == null) {
                    event.reply("❌ 유저 정보를 확인할 수 없습니다.").setEphemeral(true).queue()
                    return
                }

                val guildId = event.guild!!.id
                val channelId = event.channel.id
                val userId = member.id
                val displayName = member.effectiveName

                goldAlertService.register(guildId, channelId, userId, displayName)

                event.reply("✅ `$displayName` 님이 골드섬 알림에 등록되었습니다.")
                    .setEphemeral(true)
                    .queue()
            }

            // ❌ 골드섬 알림 해제
            "unregisteralert" -> {
                val member = event.member
                if (member == null) {
                    event.reply("❌ 유저 정보를 확인할 수 없습니다.").setEphemeral(true).queue()
                    return
                }

                val guildId = event.guild!!.id
                val channelId = event.channel.id
                val userId = member.id
                val displayName = member.effectiveName

                goldAlertService.unregister(guildId, channelId, userId)

                event.reply("🛑 `$displayName` 님의 골드섬 알림 등록이 해제되었습니다.")
                    .setEphemeral(true)
                    .queue()
            }
        }
    }
}