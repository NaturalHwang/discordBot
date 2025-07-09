package rising.bot.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GoldAlertScheduler(
    private val goldAlertService: GoldAlertService
) {
    // 1분마다 실행
    @Scheduled(fixedRate = 60_000)
    fun runGoldAlert() {
        goldAlertService.notifyRegisteredMembersBeforeEvents()
    }
}
