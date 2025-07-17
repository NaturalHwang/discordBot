package rising.bot.component

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ChannelCache(
    private val firebaseDatabase: FirebaseDatabase
) {
    private val enabledChannelMap = ConcurrentHashMap<String, String>()

    init {
        // 앱 시작 시 전체 로딩
        firebaseDatabase.getReference("enabled_channels")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    enabledChannelMap.clear()
                    for (child in dataSnapshot.children) {
                        val guildId = child.key ?: continue
                        val channelId = child.getValue(String::class.java) ?: continue
                        enabledChannelMap[guildId] = channelId
                    }
                }
                override fun onCancelled(error: DatabaseError) { }
            })
    }

    fun registerChannel(guildId: String, channelId: String) {
        firebaseDatabase.getReference("enabled_channels").child(guildId)
            .setValueAsync(channelId)
        enabledChannelMap[guildId] = channelId
    }

    fun getChannelId(guildId: String): String? = enabledChannelMap[guildId]

    fun allGuildChannelPairs(): Map<String, String> = enabledChannelMap.toMap()
}