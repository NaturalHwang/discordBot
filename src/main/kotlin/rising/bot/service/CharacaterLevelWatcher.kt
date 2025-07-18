package rising.bot.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import rising.bot.component.ChannelCache
import rising.bot.component.LoaApiClient
import rising.bot.domain.MainCharacter
import rising.bot.dto.CharacterInfoDto
import rising.bot.repository.GameCharacterRepository
import rising.bot.repository.MainCharacterRepository
import java.util.concurrent.Executors

@Service
class CharacterLevelWatcher(
    private val mainRepo: MainCharacterRepository,
    private val charRepo: GameCharacterRepository,
    private val api: LoaApiClient,
    private val discord: DiscordService,
    private val channelCache: ChannelCache
) {
    private val pool = Executors.newFixedThreadPool(10)

    @Scheduled(cron = "0 */5 * * * *")
//    fun watch() =
//        mainRepo.findAll().forEach { main ->
//            pool.execute { sync(main) }
//        }
    fun watch() {
        val allChannels = channelCache.allGuildChannelPairs()
        for ((guildId, channelId) in allChannels) {
            mainRepo.findAll(guildId, channelId).forEach { main ->
                pool.execute { sync(guildId, channelId, main) }
            }
        }
    }

    private fun CharacterInfoDto.itemLevelAsDouble(): Double =
        this.itemAvgLevel.replace(",", "").toDoubleOrNull() ?: 0.0

//    private fun sync(main: MainCharacter) {
    private fun sync(guildId: String, channelId: String, main: MainCharacter) {
        val mainName = main.name
        val remote: List<CharacterInfoDto> = api.siblings(mainName)
//        val dbList = charRepo.findByMain(main)
        val dbList = charRepo.findByMain(guildId, channelId, main)
        val remoteNames = remote.map { dto -> dto.characterName }.toSet()

        val newOrUpdated = remote.filter { dto ->
            val dbChar = dbList.find { db -> db.name == dto.characterName }
            val dbLevel = dbChar?.itemLevel ?: 0.0
            val apiLevel = dto.itemLevelAsDouble()
            dbChar == null || dbLevel != apiLevel
        }

        val deleted = dbList.filter { gc -> gc.name !in remoteNames }

        newOrUpdated.forEach { dto ->
            val dbChar = dbList.find { it.name == dto.characterName }
            val newLevel = dto.itemLevelAsDouble()
//            신규 등록의 경우 현재 레벨이 최대치
            val prevMax = dbChar?.maxItemLevel ?: newLevel

            val newMax = if (newLevel > prevMax) newLevel else prevMax

            val entity = rising.bot.domain.GameCharacter(
                id = dbChar?.id,
                mainId = main.id,
                name = dto.characterName,
                serverName = dto.serverName,
                className = dto.characterClassName,
                itemLevel = newLevel,
                maxItemLevel = newMax
            )
//            charRepo.save(entity)
            charRepo.save(guildId, channelId, entity)
            // 오로지 maxItemLevel을 갱신하는 상황에서만 알림 전송
            if (dbChar != null && newLevel > prevMax) {
                val image = api.detail(dto.characterName)?.characterImage
                if (image != null) {
                    discord.sendLevelUp(guildId, channelId, dto, image, prevMax, mainName)
                }
            }
        }
//        charRepo.deleteAll(deleted)
            charRepo.deleteAll(guildId, channelId, deleted)
    }
}