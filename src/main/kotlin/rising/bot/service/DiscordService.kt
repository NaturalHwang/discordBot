package rising.bot.service

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import rising.bot.dto.CharacterInfoDto
import java.time.Instant

@Service
class DiscordService(
    @Value("\${discord.channel-id}") private val channelId: Long,
    client: GatewayDiscordClient
) {
    private val channelMono = client.getChannelById(Snowflake.of(channelId))
        .ofType(MessageChannel::class.java)

    fun sendLevelUp(info: CharacterInfoDto, image: String, old: Double) {
        val newLevel = info.itemAvgLevel.replace(",", "").toDoubleOrNull() ?: 0.0
        val embed = EmbedCreateSpec.builder()
            .title("${info.characterName} 레벨업!")
            .description("[${info.serverName}]${info.characterName} : $old → $newLevel")
            .thumbnail("")
            .image(image)
            .timestamp(Instant.now())
            .color(Color.SEA_GREEN)
            .build()

        channelMono.flatMap { ch: MessageChannel ->
            ch.createMessage(embed)
        }.subscribe()
    }

    fun sendInfoMessage(title: String, description: String) {
        val embed = EmbedCreateSpec.builder()
            .title(title)
            .description(description)
            .color(Color.SEA_GREEN)
            .timestamp(Instant.now())
            .build()
        channelMono.flatMap { it.createMessage(embed) }.subscribe()
    }
}