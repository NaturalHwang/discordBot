package rising.bot.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import rising.bot.service.GoldAlertService
import rising.bot.service.GoldCalendarScheduler
import rising.bot.service.bunbaeService
import java.util.concurrent.TimeUnit

@Component
class MessageCommandListener(
    private val goldCalendarScheduler: GoldCalendarScheduler,
    private val bunbaeService: bunbaeService,
    @Value("\${discord.channel-id}") private val allowedChannelId: Long,
    private val goldAlertService: GoldAlertService,
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
//        봇 자신의 메시지는 무시
        if (event.author.isBot) return

//        허용된 채널 외에 작동X
        if (event.channel.idLong != allowedChannelId) return

        val content = event.message.contentRaw.trim()
        val channel = event.channel

        when {
            content == "!도움말" -> {
                val helpMessage = """
                **레벨업 감지 기능 등록**: 목표확인 << DM 주세요~!

                **!분배**: 경매 입찰 시 손익분기점과 입찰추천가를 알려줍니다.
                사용법) `!분배 거래소가격 인원`  
                예시: `!분배 120000 8`

                **!쌀섬**: 이번 주 쌀섬의 일정들을 출력합니다.

                **!쌀섬등록**: 쌀섬 스케줄 10분 전에 멘션해드리는 기능을 등록합니다
            """.trimIndent()
                channel.sendMessage(helpMessage).queue { msg ->
                    msg.delete().queueAfter(30, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }

//            "!분배 24000 8" 형태 명령어 감지
            content.startsWith("!분배") -> {
                val args = content.removePrefix("!분배").trim().split("\\s+".toRegex())
                if (args.size == 2) {
                    val gold = args[0].toLongOrNull()
                    val people = args[1].toIntOrNull()
                    if (gold == null || people == null) {
                        channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
                            // 봇 메시지 삭제 예약
                            msg.delete().queueAfter(30, TimeUnit.SECONDS)
                        }
                        // **사용자 메시지도 삭제 예약**
                        event.message.delete().queueAfter(30, TimeUnit.SECONDS)
                    } else {
                        val result = bunbaeService.bunbae(gold, people)
                        channel.sendMessage(result).queue { msg ->
                            msg.delete().queueAfter(30, TimeUnit.SECONDS)
                        }
                        event.message.delete().queueAfter(30, TimeUnit.SECONDS)
                    }
                } else {
                    channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                }
            }

            content == "!쌀섬" -> {
                goldCalendarScheduler.findGoldSchedule { result ->
                    channel.sendMessage(result).queue { msg ->
                        msg.delete().queueAfter(20, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(20, TimeUnit.SECONDS)
                }
            }

            content == "!쌀섬등록" -> {
                val member = event.member
                val mention = member?.asMention ?: event.author.asMention
                val userId = event.author.id
                val displayName = member?.effectiveName ?: event.author.name

                goldAlertService.register(userId, displayName)
                event.channel.sendMessage("$mention 님이 등록되었습니다! (닉네임: $displayName)")
                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }

            else -> {
                channel.sendMessage("알 수 없는 명령어입니다. `!도움말`을 입력해보세요!").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }
        }
//        if (content == "!도움말") {
//            val helpMessage =
//                """
//                **레벨업 감지 기능 등록**: 목표확인 << DM 주세요~!
//
//                **!분배**: 경매 입찰 시 손익분기점과 입찰추천가를 알려줍니다.
//                사용법) `!분배 거래소가격 인원`
//                예시: `!분배 120000 8`
//
//                **!쌀섬**: 이번 주 쌀섬의 일정들을 출력합니다.
//
//                **!쌀섬등록**: 쌀섬 스케줄 10분 전에 멘션해드리는 기능을 등록합니다
//            """.trimIndent()
//            channel.sendMessage(helpMessage).queue { msg ->
//                msg.delete().queueAfter(30, TimeUnit.SECONDS)
//            }
//            event.message.delete().queueAfter(30, TimeUnit.SECONDS)
//        }
//
////        "!분배 24000 8" 형태 명령어 감지
//        if (content.startsWith("!분배")) {
//            val args = content.removePrefix("!분배").trim().split("\\s+".toRegex())
//            if (args.size == 2) {
//                val gold = args[0].toLongOrNull()
//                val people = args[1].toIntOrNull()
//                if (gold == null || people == null) {
//                    channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
//                        // 봇 메시지 삭제 예약
//                        msg.delete().queueAfter(30, TimeUnit.SECONDS)
//                    }
//                    // **사용자 메시지도 삭제 예약**
//                    event.message.delete().queueAfter(30, TimeUnit.SECONDS)
//                } else {
//                    val result = bunbaeService.bunbae(gold, people)
//                    channel.sendMessage(result).queue { msg ->
//                        msg.delete().queueAfter(30, TimeUnit.SECONDS)
//                    }
//                    event.message.delete().queueAfter(30, TimeUnit.SECONDS)
//                }
//            } else {
//                channel.sendMessage("올바른 예시: `!분배 24000 8` (골드, 인원수)").queue { msg ->
//                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
//                }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//        }
//
////        "!쌀섬" 명령어 감지
//        if (content == "!쌀섬") {
//            goldCalendarScheduler.findGoldSchedule { result ->
//                channel.sendMessage(result).queue { msg ->
//                    msg.delete().queueAfter(20, TimeUnit.SECONDS)
//                }
//                // 사용자 명령어 메시지도 삭제
//                event.message.delete().queueAfter(20, TimeUnit.SECONDS)
//            }
//        }
//
//        if (content == "!쌀섬등록") {
//            // 서버에서 메시지를 보냈을 때만 존재
//            val member = event.member
//            // 서버 닉네임 기준 멘션. 서버 닉네임이 있으면 닉네임, 없으면 userName
//            val mention = member?.asMention ?: event.author.asMention
//            val userId = event.author.id
//            val displayName = member?.effectiveName ?: event.author.name
//
//            goldAlertService.register(userId,displayName)
//            event.channel.sendMessage("$mention 님이 등록되었습니다! (닉네임: $displayName)")
//                .queue { msg ->
//                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
//                }
//            event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//        }
    }
}
