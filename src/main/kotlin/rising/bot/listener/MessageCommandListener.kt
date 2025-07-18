package rising.bot.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.component.ChannelCache
import rising.bot.repository.MainCharacterRepository
import rising.bot.service.GoldAlertService
import rising.bot.service.GoldCalendarScheduler
import rising.bot.service.bunbaeService
import java.util.concurrent.TimeUnit

@Component
class MessageCommandListener(
    private val goldCalendarScheduler: GoldCalendarScheduler,
    private val bunbaeService: bunbaeService,
//    @Value("\${discord.channel-id}") private val allowedChannelId: Long,
    private val goldAlertService: GoldAlertService,
    private val channelCache: ChannelCache,
    private val repo: MainCharacterRepository,
    private val auction: AuctionService,
) : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
//        봇 자신의 메시지는 무시
        if (event.author.isBot) return

//        허용된 채널 외에 작동X
//        if (event.channel.idLong != allowedChannelId) return

        val guildId = event.guild.id
        val channelId = event.channel.id
        val content = event.message.contentRaw.trim()
        val channel = event.channel

        val baseRequest = AuctionItemSearchRequest(
            categoryCode = 0,
            sortCondition = "ASC",
            sort = "BUY_PRICE",
            itemGrade = "고대",
            itemTier = 4,
            itemGradeQuality = null,
            itemLevelMin = 0,
            itemLevelMax = 0,
            pageNo = 1,
            characterClass = "",
            itemName = "",
            etcOptions = emptyList(),
            skillOptions = emptyList()
        )

//        명령 채널 등록
        if (content == "!채널등록") {
            val member = event.member
            if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                channel.sendMessage("**이 명령어는 채널 관리자만 사용할 수 있습니다.**").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                return
            } else {
                val guildId = event.guild.id
                val channelId = event.channel.id
                val registeredChannelId = channelCache.getChannelId(guildId)
                if (registeredChannelId != null && registeredChannelId == channelId) {
                    channel.sendMessage("**이미 이 채널이 명령 채널로 등록되어 있습니다!**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                if (registeredChannelId != null && registeredChannelId != channelId) {
                    // **덮어쓰기 바로 진행**
                    channelCache.registerChannel(guildId, channelId)

                    val jda = event.jda
                    val prevChannel = jda.getTextChannelById(registeredChannelId)
                    val prevChannelName = prevChannel?.name ?: "알 수 없음"

                    channel.sendMessage("**명령 채널이 기존 '$prevChannelName'에서 이 채널로 변경되었습니다!**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                // 신규 등록
                channelCache.registerChannel(guildId, channelId)
                channel.sendMessage("**이 채널이 명령 채널로 등록되었습니다!**").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                return
            }
        }

//        명령 채널 체크(이 서버의 명령 채널에서만 동작)
        if (channelCache.getChannelId(guildId) != channelId) return

        when {
            content == "!도움말" -> {
                val helpMessage = """
                **관리자 기능**: !캐릭터등록 대표캐릭터명, !등록해제 대표캐릭터명
                
                **!경매**: 경매 입찰 시 손익분기점과 입찰추천가를 알려줍니다.
                사용법) `!경매 거래소가격 인원`  
                예시: `!경매 120000 8`

                **!쌀섬**: 이번 주 쌀섬의 일정들을 출력합니다.

                **!쌀섬등록**: 쌀섬 스케줄 10분 전에 멘션해드리는 기능을 등록합니다
                
                ""!상단일 or !상하 or !상중 or !상상: 각각 해당하는 악세의 최저가를 출력합니다(품질, 거횟 등 고려x) 
            """.trimIndent()
                channel.sendMessage(helpMessage).queue { msg ->
                    msg.delete().queueAfter(30, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }

            content.startsWith("!캐릭터등록") -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    channel.sendMessage("**이 명령어는 채널 관리자만 사용할 수 있습니다.**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                val args = content.removePrefix("!캐릭터등록").trim()
                if (args.isBlank()) {
                    channel.sendMessage("사용법: `!캐릭터등록 대표캐릭터명`").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                val mainCharacterName = args
                val guildId = event.guild.id
                val channelId = event.channel.id

//                이미 등록된 메인캐릭터 체크
                val exist = repo.findByName(guildId, channelId, mainCharacterName)
                if (exist != null) {
                    channel.sendMessage("**이미 등록된 메인 캐릭터입니다**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }

//                신규 등록
                repo.save(guildId, channelId, rising.bot.domain.MainCharacter(name = mainCharacterName))
                channel.sendMessage("메인 캐릭터 등록 완료: **$mainCharacterName**").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                return
            }

            content.startsWith("!등록해제") -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    channel.sendMessage("**이 명령어는 채널 관리자만 사용할 수 있습니다.**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                val args = content.removePrefix("!등록해제").trim()
                if (args.isBlank()) {
                    channel.sendMessage("사용법: `!등록해제 대표캐릭터명`").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }
                val mainCharacterName = args
                val guildId = event.guild.id
                val channelId = event.channel.id

                val exist = repo.findByName(guildId, channelId, mainCharacterName)
                if (exist == null) {
                    channel.sendMessage("**등록되지 않은 메인 캐릭터입니다**").queue { msg ->
                        msg.delete().queueAfter(10, TimeUnit.SECONDS)
                    }
                    event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                    return
                }

                repo.deleteByName(guildId, channelId, mainCharacterName)
                channel.sendMessage("**$mainCharacterName**님의 레벨업 감지를 해제했습니다.").queue { msg ->
                    msg.delete().queueAfter(10, TimeUnit.SECONDS)
                }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
                return
            }

//            "!경매 24000 8" 형태 명령어 감지
            content.startsWith("!경매") -> {
                val args = content.removePrefix("!경매").trim().split("\\s+".toRegex())
                if (args.size == 2) {
                    val gold = args[0].toLongOrNull()
                    val people = args[1].toIntOrNull()
                    if (gold == null || people == null) {
                        channel.sendMessage("올바른 예시: `!경매 24000 8` (골드, 인원수)").queue { msg ->
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
                    channel.sendMessage("올바른 예시: `!경매 24000 8` (골드, 인원수)").queue { msg ->
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

                val guildId = event.guild.id
                val channelId = event.channel.id
                goldAlertService.register(guildId, channelId, userId, displayName)
//                goldAlertService.register(userId, displayName)
                event.channel.sendMessage("$mention 님이 등록되었습니다! (닉네임: $displayName)")
                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
            }

            content == "!상단일" -> {
                val resultMessage = auction.handleAuctionPreset(
                    AuctionPreset.상단일,
                    baseRequest
                )
                event.channel.sendMessage(resultMessage)
                    .queue { msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }

            content == "!상하" -> {
                val resultMessage = auction.handleAuctionPreset(
                    AuctionPreset.상하,
                    baseRequest
                )
                event.channel.sendMessage(resultMessage)
                    .queue { msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }

            content == "!상중" -> {
                val resultMessage = auction.handleAuctionPreset(
                    AuctionPreset.상중,
                    baseRequest
                )
                event.channel.sendMessage(resultMessage)
                    .queue { msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }

            content == "!상상" -> {
                val resultMessage = auction.handleAuctionPreset(
                    AuctionPreset.상상,
                    baseRequest
                )
                event.channel.sendMessage(resultMessage)
                    .queue { msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS) }
                event.message.delete().queueAfter(30, TimeUnit.SECONDS)
            }
//
//            content == "!7겁" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!7작" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!8겁" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!8작" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!9겁" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!9작" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!10겁" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }
//
//            content == "!10작" -> {
//                auction.handleAuctionPreset()
//                val channelId = event.channel.id
//                event.channel.sendMessage("")
//                    .queue { msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS) }
//                event.message.delete().queueAfter(10, TimeUnit.SECONDS)
//            }

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
