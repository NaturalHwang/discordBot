package rising.bot.command.registrar

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import org.springframework.stereotype.Component

@Component
class SlashCommandManager(
    private val registrars: List<SlashCommandRegistrar>
) {
    fun registerAll(jda: JDA, guild: Guild) {
        println("총 등록기 수: ${registrars.size}")
        val allCommands = registrars.flatMap { registrar ->
            println("명령 등록기 호출: ${registrar::class.qualifiedName}")
            registrar.provideCommands()
        }

        guild.updateCommands().addCommands(allCommands).queue {
            println("✅ 모든 명령어 등록 완료 (${allCommands.size}개)")
        }
    }
}
