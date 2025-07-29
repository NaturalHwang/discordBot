package rising.bot.command.registrar

import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component

@Component
class AuctionSlashCommandRegistrar : SlashCommandRegistrar{
    override fun provideCommands(): List<CommandData> {
        return listOf(
            Commands.slash("auctioncalc", "Calculate bid recommendation for auction")
                .addOptions(
                    OptionData(OptionType.INTEGER, "gold", "현재 경매장 가격", true)
//                        이렇게 setNameLocalization 을 통해 언어 별 설정이 가능하다
                        .setNameLocalization(DiscordLocale.KOREAN, "경매장가격")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "현재 경매장 가격"),

                    OptionData(OptionType.INTEGER, "people", "경매 참여 인원", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "인원")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "경매 참여 인원 수")
                )
                .setNameLocalization(DiscordLocale.KOREAN, "경매")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "경매 입찰 추천가 계산"),

            Commands.slash("auctiondetail", "상세 경매 검색")
                .addOptions(
                    OptionData(OptionType.INTEGER, "quality", "품질 (예: 80)", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "최소품질")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "최소 품질"),

                    OptionData(OptionType.STRING, "option1", "첫 번째 옵션", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "옵션1")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "검색할 첫 번째 옵션"),

                    OptionData(OptionType.STRING, "option2", "두 번째 옵션", false)
                        .setNameLocalization(DiscordLocale.KOREAN, "옵션2")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "검색할 두 번째 옵션")
                )
                .setNameLocalization(DiscordLocale.KOREAN, "상세검색")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "악세 상세 검색"),

            Commands.slash("gem", "보석 최저가 검색")
                .addOption(OptionType.STRING, "보석", "예: 10겁, 9작 등", true)
                .setNameLocalization(DiscordLocale.KOREAN, "보석")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "보석 최저가 검색"),

            Commands.slash("expensiveengravings", "비싼 유각 검색")
                .setNameLocalization(DiscordLocale.KOREAN, "비싼유각")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "비싼 유각 상위 10개 출력"),

            Commands.slash("auctionpreset", "경매 프리셋 실행")
                .addOptions(
                    OptionData(OptionType.STRING, "preset", "적용할 경매 프리셋", true)
                        .setNameLocalization(DiscordLocale.KOREAN, "프리셋")
                        .setDescriptionLocalization(DiscordLocale.KOREAN, "적용할 프리셋을 선택하세요")
                        .addChoice("상단일", "상단일")
                        .addChoice("상하", "상하")
                        .addChoice("상중", "상중")
                        .addChoice("상상", "상상")
                        .addChoice("중중", "중중")
                )
                .setNameLocalization(DiscordLocale.KOREAN, "경매프리셋")
                .setDescriptionLocalization(DiscordLocale.KOREAN, "등록된 경매 프리셋을 바로 검색합니다")
        )
    }
}