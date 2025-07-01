package rising.bot.config

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfig {

    @Bean
    fun gatewayDiscordClient(@Value("\${discord.token}") token: String): GatewayDiscordClient {
        return DiscordClientBuilder.create(token)
            .build()
            .login()
            .block()!!
    }
}