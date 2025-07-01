package rising.bot.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import rising.bot.domain.MainCharacter
import rising.bot.dto.CharacterInfoDto
import rising.bot.loa.LoaApiClient
import rising.bot.repository.GameCharacterRepository
import rising.bot.repository.MainCharacterRepository
import java.util.concurrent.Executors

@Service
class CharacterLevelWatcher(
    private val mainRepo: MainCharacterRepository,
    private val charRepo: GameCharacterRepository,
    private val api: LoaApiClient,
    private val discord: DiscordService
) {
    private val pool = Executors.newFixedThreadPool(10)

    @Scheduled(cron = "0 */5 * * * *")
    fun watch() =
        mainRepo.findAll().forEach { main ->
            pool.execute { sync(main) }
        }

    private fun sync(main: MainCharacter) {
        val mainName = main.name
        val remote: List<CharacterInfoDto> = api.siblings(mainName)
        val dbList = charRepo.findByMain(main)
        val remoteNames = remote.map { dto -> dto.characterName }.toSet()
        val dbNames = dbList.map { gc -> gc.name }.toSet()

        val newOrUpdated = remote.filter { dto ->
            dto.characterName !in dbNames ||
                    dbList.find { db -> db.name == dto.characterName }?.itemLevel != dto.itemAvgLevel.toDoubleOrNull()
        }

        val deleted = dbList.filter { gc -> gc.name !in remoteNames }

        newOrUpdated.forEach { dto ->
            val dbChar = dbList.find { it.name == dto.characterName }
            val entity = rising.bot.domain.GameCharacter(
                id = dbChar?.id,
                mainId = main.id,
                name = dto.characterName,
                serverName = dto.serverName,
                className = dto.characterClassName,
                itemLevel = dto.itemAvgLevel.replace(",", "").toDoubleOrNull() ?: 0.0
            )
            charRepo.save(entity)
            val image = api.detail(dto.characterName)?.characterImage
            if (dbChar != null && dbChar.itemLevel < (dto.itemAvgLevel.replace(",", "").toDoubleOrNull() ?: 0.0)) {
                if (image != null) {
                    discord.sendLevelUp(dto, image, dbChar.itemLevel)
                }
            }
        }
        charRepo.deleteAll(deleted)
    }
}