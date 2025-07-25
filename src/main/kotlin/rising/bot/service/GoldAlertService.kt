package rising.bot.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Service
import rising.bot.component.ChannelCache
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class GoldAlertService(
    private val firebaseDatabase: FirebaseDatabase,
    private val jdaProvider: ObjectProvider<JDA>,
//    @Value("\${discord.channel-id}") private val channelId: Long
    private val channelCache: ChannelCache
) {
    // 골드 멤버 등록 (비동기)
//    fun register(userId: String, displayName: String) {
//        val ref = firebaseDatabase.getReference("gold-alert-member")
//        ref.child(userId).setValueAsync(displayName)
//    }

    fun register(guildId: String, channelId: String, userId: String, displayName: String) {
        firebaseDatabase.getReference("channels")
            .child(guildId)
            .child(channelId)
            .child("gold-alert-member")
            .child(userId)
            .setValueAsync(displayName)
    }

//    알림 등록 해제
    fun unregister(guildId: String, channelId: String, userId: String) {
        firebaseDatabase.getReference("channels")
            .child(guildId)
            .child(channelId)
            .child("gold-alert-member")
            .child(userId)
            .removeValueAsync()
    }

    // 골드 일정 10분 전이면 등록 멤버 모두 알림
    fun notifyRegisteredMembersBeforeEvents() {
        val now = ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
//        일정은 전역에서 관리
//        val eventsRef = firebaseDatabase.getReference("gold-schedules/current-week")
        val eventsRef = firebaseDatabase.getReference("global/gold-schedules/current-week")
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(eventsSnapshot: DataSnapshot) {
//                캐싱된 guildId-channelId 목록(Map<String,String>)
                val allChannels = channelCache.allGuildChannelPairs()
                val sentMessages = mutableSetOf<String>()

                for ((guildId, channelId) in allChannels) {
                    val memberRef = firebaseDatabase.getReference("channels")
                        .child(guildId)
                        .child(channelId)
                        .child("gold-alert-member")
                    memberRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(memberSnapshot: DataSnapshot) {
                            val mentions = memberSnapshot.children.mapNotNull { it.key }
                                .joinToString(" ") { "<@$it>" }
                            if (mentions.isBlank()) return

                            for (eventSnapshot in eventsSnapshot.children) {
                                val eventMap = eventSnapshot.value as? Map<*, *> ?: continue
                                val contentsName = eventMap["contentsName"] as? String ?: continue
                                val rewardName = eventMap["rewardName"] as? String ?: continue
                                val startTimeStr = eventMap["startTime"] as? String ?: continue

                                val eventStartTime = try{
                                    ZonedDateTime.parse(startTimeStr, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                                } catch (e: Exception) { null } ?: continue
                                val minutesUntilEvent = java.time.Duration.between(now, eventStartTime).toMinutes()

                                // 같은 알림 2번 발송되는 이슈 조치
                                val eventId = eventSnapshot.key ?: (contentsName + startTimeStr)
                                val msgKey = "$channelId-$eventId"
                                if (sentMessages.contains(msgKey)) continue
                                sentMessages.add(msgKey)

                                if (minutesUntilEvent in 9..10) {
                                    val message = "$mentions\n[$contentsName] 보상: $rewardName\n시작 10분 전입니다! 준비하세요."
                                    val jda = jdaProvider.getObject()
                                    val channel = jda.getTextChannelById(channelId)
                                    channel?.sendMessage(message)?.queue { msg ->
                                        msg.delete().queueAfter(5, java.util.concurrent.TimeUnit.MINUTES)
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) { }
                    })
                }
//                for (eventSnapshot in eventsSnapshot.children) {
//                    val eventMap = eventSnapshot.value as? Map<*, *> ?: continue
//                    val contentsName = eventMap["contentsName"] as? String ?: continue
//                    val rewardName = eventMap["rewardName"] as? String ?: continue
//                    val startTimeStr = eventMap["startTime"] as? String ?: continue
//
//                    // "2025-07-09T16:11+09:00[Asia/Seoul]" → ZonedDateTime 파싱
//                    val eventStartTime = ZonedDateTime.parse(startTimeStr, DateTimeFormatter.ISO_ZONED_DATE_TIME)
//                    val minutesUntilEvent = java.time.Duration.between(now, eventStartTime).toMinutes()
//
//                    if (minutesUntilEvent in 9..10) {
//                        val memberRef = firebaseDatabase.getReference("gold-alert-member")
//                        memberRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val mentions = snapshot.children.mapNotNull { it.key }
//                                    .joinToString(" ") { "<@$it>" } // userId로 멘션 문자열 생성
//                                if (mentions.isBlank()) return
//                                val message = "$mentions\n[$contentsName] 보상: $rewardName\n시작 10분 전입니다! 준비하세요."
//                                val jda = jdaProvider.getObject()
//                                val channel = jda.getTextChannelById(channelId)
//                                channel?.sendMessage(message)?.queue { msg ->
//                                    msg.delete().queueAfter(5, java.util.concurrent.TimeUnit.MINUTES)
//                                }
//                            }
//                            override fun onCancelled(error: DatabaseError) { }
//                        })
//                    }
//                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }
}