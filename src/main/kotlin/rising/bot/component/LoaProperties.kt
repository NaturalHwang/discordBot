package rising.bot.component

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "loa")
data class LoaProperties(
    val tokens: List<String> = emptyList()
)