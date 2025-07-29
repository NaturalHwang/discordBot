package rising.bot.command.registrar

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class RaidSlashCommandRegistrar : SlashCommandRegistrar {
    override fun provideCommands(): List<CommandData> {
        return listOf(
            Commands.slash("damagerate", "딜 지분 계산기")
                .addOptions(
                    OptionData(OptionType.STRING, "난이도", "레이드 난이도를 선택하세요", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "난이도")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "레이드 난이도를 선택하세요")
                        .addChoice("노말", "노말")
                        .addChoice("하드", "하드"),

                    OptionData(OptionType.STRING, "레이드", "막 이름을 선택하세요 (예: 1막, 익카)", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "레이드")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "막 이름을 선택하세요")
                        .addChoice("서막", "서막")
                        .addChoice("1막", "1막")
                        .addChoice("2막", "2막")
                        .addChoice("3막", "3막")
                        .addChoice("익카", "익카"),

                    OptionData(OptionType.INTEGER, "관문", "관문 번호", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "관문")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "관문 번호를 입력하세요")
                        .addChoice("1관문", 1)
                        .addChoice("2관문", 2)
                        .addChoice("3관문", 3),

                    OptionData(OptionType.NUMBER, "딜량", "당신의 총 딜량을 입력하세요", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "딜량")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "총 딜량을 입력하세요")
                )
                .setNameLocalization(DiscordLocale.KOREAN, "딜지분")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "딜량을 기반으로 기여도를 계산합니다")
        )
    }
}