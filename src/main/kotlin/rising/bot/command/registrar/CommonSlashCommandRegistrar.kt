package rising.bot.command.registrar

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import org.springframework.stereotype.Component

@Component
class CommonSlashCommandRegistrar : SlashCommandRegistrar {
    override fun provideCommands(): List<CommandData> {
        return listOf(
            Commands.slash("help", "도움말 보기")
                .setNameLocalization(DiscordLocale.KOREAN, "도움말")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "사용할 수 있는 명령어를 보여줍니다"),

            // 대표 캐릭터 등록
            Commands.slash("registercharacter", "레벨업 감지 기능 등록")
                .addOption(OptionType.STRING, "캐릭터명", "캐릭터 이름", true)
                .setNameLocalization(DiscordLocale.KOREAN, "캐릭터등록")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "대표 캐릭터를 등록합니다"),

            // 대표 캐릭터 등록 해제
            Commands.slash("unregistercharacter", "레벨업 감지 기능 해제")
                .addOption(OptionType.STRING, "캐릭터명", "캐릭터 이름", true)
                .setNameLocalization(DiscordLocale.KOREAN, "등록해제")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "대표 캐릭터 등록을 해제합니다"),

            // 명령 채널 등록
            Commands.slash("setcommandchannel", "명령어 채널 지정")
                .setNameLocalization(DiscordLocale.KOREAN, "채널등록")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "현재 채널을 명령 채널로 등록합니다"),

            // API 키 등록
            Commands.slash("registerapi", "api키 등록")
                .addOption(OptionType.STRING, "api키", "Loa API key", true)
                .setNameLocalization(DiscordLocale.KOREAN, "api등록")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "Loa API 키를 등록합니다"),
        )
    }
}