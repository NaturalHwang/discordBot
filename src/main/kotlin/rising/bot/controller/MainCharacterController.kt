package rising.bot.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rising.bot.domain.MainCharacter
import rising.bot.dto.RegisterMainCharacterRequest
import rising.bot.dto.RegisterMainCharacterResponse
import rising.bot.repository.GameCharacterRepository
import rising.bot.repository.MainCharacterRepository

@RestController
@RequestMapping("/api/main-character")
class MainCharacterController(
    private val repo: MainCharacterRepository,
    private val gameCharacterRepo: GameCharacterRepository
) {
    @PostMapping
    fun register(@RequestBody req: RegisterMainCharacterRequest): ResponseEntity<RegisterMainCharacterResponse> {
        if (repo.findByName(req.name) != null) {
            return ResponseEntity.badRequest().build()
        }
        val id = repo.save(MainCharacter(name = req.name))
        return ResponseEntity.ok(RegisterMainCharacterResponse(name = req.name))
    }

    @DeleteMapping
    fun deleteByMain(@RequestParam main: String): ResponseEntity<Void> {
        val mainChar = repo.findByName(main)
        if (mainChar == null) {
            return ResponseEntity.notFound().build()
        }
        gameCharacterRepo.deleteByMain(mainChar)
        repo.deleteByName(mainChar.name)
        return ResponseEntity.ok().build()
    }
}
