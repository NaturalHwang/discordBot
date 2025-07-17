package rising.bot.config

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rising.bot.listener.MessageCommandListener

@Configuration
class DiscordConfig(
//    private val slashCommandListener: SlashCommandListener,
    private val messageCommandListener: MessageCommandListener,
//    @Value("\${discord.channel-id}") private val channelId: String
) {
    @Bean
    fun jda(@Value("\${discord.token}") botToken: String) =
        JDABuilder.createDefault(botToken)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(messageCommandListener)
//            .addEventListeners(slashCommandListener)
            .build()
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
}