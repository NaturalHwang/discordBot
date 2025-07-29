package rising.bot.command.registrar

import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface SlashCommandRegistrar {
    fun provideCommands(): List<CommandData>
}