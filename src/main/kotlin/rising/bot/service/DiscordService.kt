package rising.bot.service

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import org.springframework.stereotype.Service
import rising.bot.dto.CharacterInfoDto
import java.awt.Color
import java.time.Instant

@Service
class DiscordService(
//    @Value("\${discord.channel-id}") private val channelId: Long,
    private val jda: JDA
) {

//    private fun getTextChannel(): TextChannel? = jda.getTextChannelById(channelId)
    private fun getTextChannel(channelId: Long): TextChannel? =
        jda.getTextChannelById(channelId)

//    fun sendLevelUp(info: CharacterInfoDto, image: String, oldMax: Double) {
    fun sendLevelUp(
        guildId: String,
        channelId: String,
        info: CharacterInfoDto,
        image: String,
        oldMax: Double,
        mainName: String
    ) {
        val newLevel = info.itemAvgLevel.replace(",", "").toDoubleOrNull() ?: 0.0

        val embed = EmbedBuilder()
            .setTitle("${info.characterName}(${mainName}) 레벨업!")
            .setDescription("[${info.serverName}]${info.characterName} : $oldMax → $newLevel")
            .setImage(image)
            .setTimestamp(Instant.now())
            .setColor(Color(46, 204, 113))
            .build()

        getTextChannel(channelId.toLong())?.sendMessageEmbeds(embed)?.queue()
    }

//    fun sendInfoMessage(title: String, description: String) {
    fun sendInfoMessage(
        guildId: String,
        channelId: String,
        title: String,
        description: String
    ) {

        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription(description)
            .setColor(Color(46, 204, 113))
            .setTimestamp(Instant.now())
            .build()
        getTextChannel(channelId.toLong())?.sendMessageEmbeds(embed)?.queue()
    }
}