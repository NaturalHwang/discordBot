package rising.bot.command.registrar

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component

@Component
class CalendarIslandSlashCommandRegistrar : SlashCommandRegistrar {
    override fun provideCommands(): List<CommandData> {
        return listOf(
            Commands.slash("goldisland", "이번 주 쌀섬 일정 출력")
                .setNameLocalization(DiscordLocale.KOREAN, "쌀섬")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "이번 주 골드섬 일정을 출력합니다"),

            Commands.slash("registeralert", "쌀섬 멘션 알림 등록")
                .setNameLocalization(DiscordLocale.KOREAN, "쌀섬등록")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "골드섬 알림을 등록합니다"),

            Commands.slash("unregisteralert", "쌀섬 멘션 알림 해제")
                .setNameLocalization(DiscordLocale.KOREAN, "알림해제")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "골드섬 알림을 해제합니다")
        )
    }
}