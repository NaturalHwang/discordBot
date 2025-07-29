package rising.bot.config

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rising.bot.command.listener.*
import rising.bot.command.registrar.SlashCommandRegistrar

@Configuration
class DiscordConfig(
    private val commonSlashCommandListener: CommonSlashCommandListener,
    private val messageCommandListener: MessageCommandListener,
//    @Value("\${discord.channel-id}") private val channelId: String
//    private val userContextListener: UserContextListener
    private val slashCommandRegistrars: List<SlashCommandRegistrar>,
    private val auctionSlashCommandListener: AuctionSlashCommandListener,
    private val raidSlashCommandListener: RaidSlashCommandListener,
    private val calendarIslandSlashCommandListener: CalendarIslandSlashCommandListener,
) {
    @Bean
//    fun jda(@Value("\${discord.token}") botToken: String) =
    fun jda(@Value("\${discord.token}") botToken: String): JDA {
        val jda = JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(
                messageCommandListener,
                commonSlashCommandListener,
                auctionSlashCommandListener,
                raidSlashCommandListener,
                calendarIslandSlashCommandListener,
            )
//            .addEventListeners(slashCommandListener)
            .build()
        jda.awaitReady()

        val guild = jda.getGuildById("963099435867459634")
            ?: error("❌ 테스트용 길드를 찾을 수 없습니다.")

        println("💡 명령어 등록 대상 길드: ${guild.name} (${guild.id})")

        // 등록기들 호출
        val allCommands = slashCommandRegistrars.flatMap { it.provideCommands() }

        guild.updateCommands().addCommands(allCommands).queue {
            println("✅ Slash 명령어 ${allCommands.size}개 등록 완료")
        }

        return jda
    }
//    슬래쉬 커맨드는 한글 명령어 등록 불가(공식 문서)
//            .also { jda ->
//                jda.awaitReady()
//                val guild = jda.getGuildById(channelId)
//                guild?.upsertCommand(
//                    Commands.slash("쌀섬", "골드 일정 안내")
//                )?.queue()
//                guild?.upsertCommand(
//                    Commands.slash("분배", "분배금 계산")
//                        .addOption(OptionType.INTEGER, "gold", "가격(골드)", true)
//                        .addOption(OptionType.INTEGER, "people", "인원 수", true)
//                )?.queue()
//            }
//            .also { jda ->
//                jda.awaitReady()
//
//                // 테스트할 길드 ID 사용
//                val guild = jda.getGuildById("963099435867459634")
//                println("💡 Guild: ${guild?.name} (${guild?.id})")
//
//                guild?.updateCommands()?.addCommands(
//                    Commands.user("도움말 보기"),
//
//                    // 도움말
//                    Commands.slash("help", "Show available commands")
//                        .setNameLocalization(DiscordLocale.KOREAN, "도움말")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "사용할 수 있는 명령어를 보여줘요"),
//
//                    // 대표 캐릭터 등록
//                    Commands.slash("registercharacter", "레벨업 감지 기능 등록")
//                        .addOption(OptionType.STRING, "캐릭터명", "캐릭터 이름", true)
//                        .setNameLocalization(DiscordLocale.KOREAN, "캐릭터등록")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "대표 캐릭터를 등록합니다"),
//
//                    // 대표 캐릭터 등록 해제
//                    Commands.slash("unregistercharacter", "레벨업 감지 기능 해제")
//                        .addOption(OptionType.STRING, "캐릭터명", "캐릭터 이름", true)
//                        .setNameLocalization(DiscordLocale.KOREAN, "등록해제")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "대표 캐릭터 등록을 해제합니다"),
//
//                    // 명령 채널 등록
//                    Commands.slash("setcommandchannel", "명령어 채널 지정")
//                        .setNameLocalization(DiscordLocale.KOREAN, "채널등록")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "현재 채널을 명령 채널로 등록합니다"),
//
//                    // API 키 등록
//                    Commands.slash("registerapi", "api키 등록")
//                        .addOption(OptionType.STRING, "api키", "Loa API key", true)
//                        .setNameLocalization(DiscordLocale.KOREAN, "api등록")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "Loa API 키를 등록합니다"),
//
//                    Commands.slash("goldisland", "이번 주 쌀섬 일정 안내")
//                        .setNameLocalization(DiscordLocale.KOREAN, "쌀섬")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "이번 주 골드 섬 일정을 보여줍니다"),
//
//                    Commands.slash("registergoldislandalert", "쌀섬 알림 등록")
//                        .setNameLocalization(DiscordLocale.KOREAN, "쌀섬등록")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "쌀섬 일정 10분 전에 알림을 받을 수 있도록 등록합니다"),
//
//                    Commands.slash("unregistergoldislandalert", "쌀섬 알림 해체")
//                        .setNameLocalization(DiscordLocale.KOREAN, "알림해제")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "쌀섬 알림 기능을 해제합니다."),
//
//                    Commands.slash("auctionprofit", "쌀산기")
//                        .addOption(OptionType.INTEGER, "경매장가격", "현재 가격", true)
//                        .addOption(OptionType.INTEGER, "인원", "경매 참여 인원 수", true)
//                        .setNameLocalization(DiscordLocale.KOREAN, "경매")
//                        .setDescriptionLocalization(DiscordLocale.KOREAN, "경매 손익분기점 및 입찰 추천가 계산")
//                )?.submit()
////                    ?.thenAccept { commands ->
////                        println("✅ 명령어 등록 성공:")
////                        commands.forEach { println("- ${it.name}") }
////                    }
////                    ?.exceptionally {
////                        println("❌ 명령어 등록 실패:")
////                        it.printStackTrace()
////                        null
////                    }
//            }
}