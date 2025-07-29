package rising.bot.command.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.component.ChannelCache
import rising.bot.component.LoaApiClient
import rising.bot.repository.MainCharacterRepository
import rising.bot.service.GoldAlertService
import rising.bot.service.GoldCalendarScheduler
import rising.bot.service.bunbaeService

@Component
class CommonSlashCommandListener(
    private val goldCalendarScheduler: GoldCalendarScheduler,
    private val channelCache: ChannelCache,
    private val repo: MainCharacterRepository,
    private val loaApi: LoaApiClient,
    private val goldAlertService: GoldAlertService,
    private val bunbaeService: bunbaeService,
    ) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id ?: return
        val channelId = event.channel.id

        when (event.name) {
//            "쌀섬" -> {
//                goldCalendarScheduler.findGoldSchedule { result   ->
//                    event.reply(result).queue()
//                }
//            }
//            "분배" -> {
//                val gold = event.getOption("gold")?.asLong
//                val people = event.getOption("people")?.asInt
//
//                if (gold == null || people == null) {
//                    event.reply("가격과 인원 수 모두 입력해주세요.").setEphemeral(true).queue()
//                    return
//                }
//                val result = bunbaeService.bunbae(gold, people)
//                event.reply(result).queue()
            "help" -> {
                event.reply("""
                ✅ 사용 가능한 명령어:
                - `/경매`: 입찰 추천가 계산
                - `/쌀섬`: 골드 캘린더 확인
                - `/도움말`: 이 메시지를 다시 봅니다
                """.trimIndent()).setEphemeral(true).queue()
            }

            "registercharacter" -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    event.reply("❌ 관리자 권한이 필요합니다.").setEphemeral(true).queue()
                    return
                }

                val charName = event.getOption("name")?.asString?.trim()
                if (charName.isNullOrBlank()) {
                    event.reply("⚠️ 캐릭터 이름을 입력해주세요.").setEphemeral(true).queue()
                    return
                }

                if (repo.findByName(guildId, channelId, charName) != null) {
                    event.reply("이미 등록된 메인 캐릭터입니다.").setEphemeral(true).queue()
                    return
                }

                repo.save(guildId, channelId, rising.bot.domain.MainCharacter(name = charName))
                event.reply("✅ 메인 캐릭터 등록 완료: **$charName**").queue()
            }

            "unregistercharacter" -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    event.reply("❌ 관리자 권한이 필요합니다.").setEphemeral(true).queue()
                    return
                }

                val charName = event.getOption("name")?.asString?.trim()
                if (charName.isNullOrBlank()) {
                    event.reply("⚠️ 캐릭터 이름을 입력해주세요.").setEphemeral(true).queue()
                    return
                }

                if (repo.findByName(guildId, channelId, charName) == null) {
                    event.reply("등록되지 않은 메인 캐릭터입니다.").setEphemeral(true).queue()
                    return
                }

                repo.deleteByName(guildId, channelId, charName)
                event.reply("✅ `$charName`의 등록이 해제되었습니다.").queue()
            }

            "setcommandchannel" -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    event.reply("❌ 관리자 권한이 필요합니다.").setEphemeral(true).queue()
                    return
                }

                val prevChannelId = channelCache.getChannelId(guildId)
                channelCache.registerChannel(guildId, channelId)

                if (prevChannelId != null && prevChannelId != channelId) {
                    val prevName = event.jda.getTextChannelById(prevChannelId)?.name ?: "알 수 없음"
                    event.reply("✅ 명령 채널이 `$prevName`에서 이 채널로 변경되었습니다.").queue()
                } else {
                    event.reply("✅ 이 채널이 명령 채널로 등록되었습니다.").queue()
                }
            }

            "registerapi" -> {
                val member = event.member
                if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_CHANNEL)) {
                    event.reply("❌ 관리자 권한이 필요합니다.").setEphemeral(true).queue()
                    return
                }

                val apiKey = event.getOption("key")?.asString?.trim()
                if (apiKey.isNullOrBlank()) {
                    event.reply("⚠️ API 키를 입력해주세요.").setEphemeral(true).queue()
                    return
                }

                val success = loaApi.registerApiKey(guildId, channelId, apiKey)
                if (success) {
                    event.reply("✅ API 키가 안전하게 등록되었습니다.").queue()
                } else {
                    event.reply("❌ 등록에 실패했거나 이미 등록된 키입니다.").queue()
                }
            }

        }
    }
}