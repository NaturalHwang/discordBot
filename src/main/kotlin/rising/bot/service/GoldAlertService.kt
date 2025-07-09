package rising.bot.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import net.dv8tion.jda.api.JDA
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
class GoldAlertService(
    private val firebaseDatabase: FirebaseDatabase,
    private val jdaProvider: ObjectProvider<JDA>,
    @Value("\${discord.channel-id}") private val channelId: Long
) {
    // 골드 멤버 등록 (비동기)
    fun register(userId: String, displayName: String) {
        val ref = firebaseDatabase.getReference("gold-alert-member")
        ref.child(userId).setValueAsync(displayName)
    }

    // 골드 일정 10분 전이면 등록 멤버 모두 알림
    fun notifyRegisteredMembersBeforeEvents() {
        val now = ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"))
        val eventsRef = firebaseDatabase.getReference("gold-schedules/current-week")
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(eventsSnapshot: DataSnapshot) {
                for (eventSnapshot in eventsSnapshot.children) {
                    val eventMap = eventSnapshot.value as? Map<*, *> ?: continue
                    val contentsName = eventMap["contentsName"] as? String ?: continue
                    val rewardName = eventMap["rewardName"] as? String ?: continue
                    val startTimeStr = eventMap["startTime"] as? String ?: continue

                    // "2025-07-09T16:11+09:00[Asia/Seoul]" → ZonedDateTime 파싱
                    val eventStartTime = ZonedDateTime.parse(startTimeStr, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                    val minutesUntilEvent = java.time.Duration.between(now, eventStartTime).toMinutes()

                    if (minutesUntilEvent in 9..10) {
                        val memberRef = firebaseDatabase.getReference("gold-alert-member")
                        memberRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val mentions = snapshot.children.mapNotNull { it.key }
                                    .joinToString(" ") { "<@$it>" } // userId로 멘션 문자열 생성
                                println("mentions: $mentions")
                                if (mentions.isBlank()) return
                                val message = "$mentions\n[$contentsName] 보상: $rewardName\n시작 10분 전입니다! 준비하세요."
                                val jda = jdaProvider.getObject()
                                val channel = jda.getTextChannelById(channelId)
                                channel?.sendMessage(message)?.queue { msg ->
                                    msg.delete().queueAfter(5, java.util.concurrent.TimeUnit.MINUTES)
                                }
                            }
                            override fun onCancelled(error: DatabaseError) { }
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }
}