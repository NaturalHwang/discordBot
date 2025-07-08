package rising.bot.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.service.GoldCalendarScheduler
import rising.bot.service.bunbaeService
import java.util.concurrent.TimeUnit

@Component
class MessageCommandListener(
    private val goldCalendarScheduler: GoldCalendarScheduler,
    private val bunbaeService: bunbaeService
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
//        봇 자신의 메시지는 무시
        if (event.author.isBot) return

        val content = event.message.contentRaw.trim()
        val channel = event.channel

//        "!분배 24000 8" 형태 명령어 감지
        if (content.startsWith("!분배")) {
            val args = content.removePrefix("!분배").trim().split("\\s+".toRegex())
            if (args.size == 2) {
                val gold = args[0].toLongOrNull()
                val people = args[1].toIntOrNull()
                if (gold == null || people == null) {
                    channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
                        // 봇 메시지 삭제 예약
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    // **사용자 메시지도 삭제 예약**
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                } else {
                    val result = bunbaeService.bunbae(gold, people)
                    channel.sendMessage(result).queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                }
            } else {
                channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }
        }

//        "!쌀섬" 명령어 감지
        if (content == "!쌀섬") {
            goldCalendarScheduler.findGoldSchedule { result ->
                channel.sendMessage(result).queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                // 사용자 명령어 메시지도 삭제
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }
        }
    }
}
