package rising.bot.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import rising.bot.loa.LoaApiClient
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors

@Service
class GoldCalendarScheduler(
    private val loaApiClient: LoaApiClient,
    private val firebaseDatabase: FirebaseDatabase
) {
    private val pool = Executors.newFixedThreadPool(2)

    @Scheduled(cron = "0 1 10 ? * WED", zone = "Asia/Seoul")
    fun scheduleGoldSave() {
        pool.execute { saveGoldSchedules() }
    }

    private fun saveGoldSchedules() {
        val calendars = loaApiClient.calendar()

        val now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val start = now.withHour(10).withMinute(1).withSecond(0).withNano(0)
        val end = start.plusDays(6).withHour(5).withMinute(59).withSecond(0).withNano(0)

        val goldSchedules = calendars.flatMap { cal ->
            cal.rewardItems
                .filter { it.name == "골드" && !it.startTimes.isNullOrEmpty() }
                .flatMap { reward ->
                    reward.startTimes?.mapNotNull { timeStr ->
                        val dateTime = ZonedDateTime.parse(timeStr, DateTimeFormatter.ISO_DATE_TIME)
                        if (dateTime.isAfter(start) && dateTime.isBefore(end)) {
                            GoldSchedule(cal.contentsName, reward.name, dateTime.toString())
                        } else null
                    } ?: listOf()
                }
        }

        val ref = firebaseDatabase.getReference("gold-schedules/current-week")
        ref.setValueAsync(goldSchedules)
    }

    fun findGoldSchedule(callback: (String) -> Unit) {
        pool.execute {
            val ref = firebaseDatabase.getReference("gold-schedules/current-week")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val schedules = snapshot.children.mapNotNull { it.getValue(GoldSchedule::class.java) }
                    val msg = if (schedules.isEmpty()) {
                        "이번 주 골드를 주는 일정이 없습니다."
                    } else {
                        schedules.joinToString("\n\n") {
                            "[${it.contentsName}] 시작 시간: ${it.startTime}"
                        }
                    }
                    callback(msg)
                }
                override fun onCancelled(error: DatabaseError) {
                    callback("Firebase 조회에 실패했습니다.")
                }
            })
        }
    }
}

data class GoldSchedule(
    val contentsName: String = "",
    val rewardName: String = "",
    val startTime: String = ""
)
