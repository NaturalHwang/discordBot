package rising.bot.command.listener

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.springframework.stereotype.Component
import rising.bot.preset.AuctionPreset
import rising.bot.service.AuctionService
import rising.bot.service.bunbaeService
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
class AuctionSlashCommandListener(
    private val bunbaeService: bunbaeService,
    private val auction: AuctionService
) : ListenerAdapter() {
    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        val guildId = event.guild?.id ?: return
        val channelId = event.channel.id

        when(event.name) {
            "auctioncalc" -> {
                val gold = event.getOption("gold")?.asLong
                val people = event.getOption("people")?.asInt

                if (gold == null || gold <= 0 || people == null || people <= 0) {
                    event.reply("⚠️ 올바른 값을 입력해주세요. 예: `/auction 경매장가격: 24000 인원: 8`")
                        .setEphemeral(true).queue()
                    return
                }

                val result = bunbaeService.bunbae(gold, people)
                event.reply(result).setEphemeral(true).queue()
            }

            "auctiondetail" -> {
                val quality = event.getOption("quality")?.asInt
                val option1 = event.getOption("option1")?.asString
                val option2 = event.getOption("option2")?.asString

                val replyMessage = if (quality == null || option1.isNullOrBlank()) {
                    "⚠️ `품질`과 `옵션1`은 필수입니다. 예시: `/auctiondetail quality:80 option1:치피상`"
                } else {
                    auction.detailAuctionSearch(quality, option1, option2, guildId, channelId)
                }

                event.reply(replyMessage).setEphemeral(true).queue()
            }

            "gem" -> {
                val allOptions = event.options
                val gemInput = event.getOption("gemtype")?.asString?.replace("\\s".toRegex(), "")

                if (gemInput == null) {
                    event.reply("⚠️ 옵션에서 '종류'를 찾을 수 없습니다. 올바른 명령어를 입력했는지 확인해 주세요.")
                        .setEphemeral(true).queue()
                    return
                }
                event.deferReply(true).queue() // 응답 지연 알림

                val result = try {
                    auction.findGemMinPrice(gemInput, guildId, channelId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    "❌ 오류 발생: ${e.message}"
                }

                event.hook.sendMessage(result).queue()
            }

            "expensiveengravings" -> {
                val result = auction.getExpensiveEngravingBooks(guildId, channelId)
                event.reply(result).setEphemeral(true).queue()
            }

            "auctionpreset" -> {
                val presetName = event.getOption("preset")?.asString
                val preset = when (presetName) {
                    "상단일" -> AuctionPreset.상단일
                    "상하" -> AuctionPreset.상하
                    "상중" -> AuctionPreset.상중
                    "상상" -> AuctionPreset.상상
                    "중중" -> AuctionPreset.중중
                    else -> {
                        event.reply("❌ 알 수 없는 프리셋입니다: `$presetName`").setEphemeral(true).queue()
                        return
                    }
                }

                val executor = Executors.newSingleThreadExecutor()

                // deferReply 즉시 호출
                event.deferReply().queue()
                // 백그라운드에서 비동기 실행
                CompletableFuture.supplyAsync({
                    auction.handleAuctionPreset(preset, guildId, channelId)
                }, executor).thenAccept { result ->
                    event.hook.editOriginal(result)
                        .delay(30, TimeUnit.SECONDS) // 30초 후
                        .flatMap { msg -> event.hook.deleteOriginal() }
                        .queue()
                }.exceptionally { e ->
                    event.hook.editOriginal("❌ 오류 발생: ${e.message}")
                        .delay(30, TimeUnit.SECONDS) // 오류 메시지도 30초 후 삭제
                        .flatMap { msg -> event.hook.deleteOriginal() }
                        .queue()
                    null
                }

            }
        }
    }
}