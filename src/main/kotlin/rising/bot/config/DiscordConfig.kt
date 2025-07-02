package rising.bot.config

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rising.bot.listener.SlashCommandListener

@Configuration
class DiscordConfig (
    private val slashCommandListener: SlashCommandListener
) {
    @Bean
    fun jda(@Value("\${discord.token}") botToken: String) =
        JDABuilder.createDefault(botToken)
            .addEventListeners(slashCommandListener)
            .setActivity(Activity.playing(""))
            .build()
            .also { jda ->
                jda.awaitReady()
                // 슬래시 명령어 여러개 등록
                jda.upsertCommand("쌀섬", "골드 일정 안내").queue()
            }
}