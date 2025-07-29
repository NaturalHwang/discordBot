package rising.bot.preset

enum class PresetType(val displayName: String, val presets: List<NamePreset>) {
    상단일("상단일", AuctionPreset.상단일),
    중중("중중", AuctionPreset.중중),
    상하("상하", AuctionPreset.상하),
    상중("상중", AuctionPreset.상중),
    상상("상상", AuctionPreset.상상)
}