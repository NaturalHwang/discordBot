package rising.bot.domain

data class MainCharacter(
    var id: String? = null, // Firebase의 key (PushKey)
    var name: String = ""
)

data class GameCharacter(
    var id: String? = null,
    var mainId: String? = null,
    var name: String = "",
    var serverName: String = "",
    var className: String = "",
    var itemLevel: Double = 0.0,
    var maxItemLevel: Double = 0.0,
)