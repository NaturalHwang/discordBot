package rising.bot.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import rising.bot.domain.MainCharacter
import rising.bot.dto.RegisterMainCharacterRequest
import rising.bot.dto.RegisterMainCharacterResponse
import rising.bot.repository.MainCharacterRepository

@RestController
@RequestMapping("/api/main-character")
class MainCharacterController(
    private val repo: MainCharacterRepository
) {
    @PostMapping
    fun register(@RequestBody req: RegisterMainCharacterRequest): ResponseEntity<RegisterMainCharacterResponse> {
        if (repo.findByName(req.name) != null) {
            return ResponseEntity.badRequest().build()
        }
        val id = repo.save(MainCharacter(name = req.name))
        return ResponseEntity.ok(RegisterMainCharacterResponse(name = req.name))
    }
}
