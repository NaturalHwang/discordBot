//package rising.bot.listener
//
//import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
//import net.dv8tion.jda.api.hooks.ListenerAdapter
//import org.springframework.stereotype.Component
//import rising.bot.service.GoldCalendarScheduler
//import rising.bot.service.bunbaeService
//
//@Component
//class SlashCommandListener(
//    private val goldCalendarScheduler: GoldCalendarScheduler,
//    private val bunbaeService: bunbaeService
//) : ListenerAdapter() {
//
//    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
//        when (event.name) {
//            "쌀섬" -> {
//                goldCalendarScheduler.findGoldSchedule { result ->
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
//            }
//            else -> {
//                event.reply("알 수 없는 명령어입니다.").setEphemeral(true).queue()
//            }
//        }
//    }
//}