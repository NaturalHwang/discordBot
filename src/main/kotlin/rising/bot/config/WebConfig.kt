package rising.bot.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
class WebConfig {
    @Bean
    fun characterEncodingFilter(): CharacterEncodingFilter {
        val filter = CharacterEncodingFilter()
        filter.encoding = "UTF-8"
        filter.setForceEncoding(true)
        return filter
    }
}