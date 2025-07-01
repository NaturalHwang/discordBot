package rising.bot.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.springframework.stereotype.Repository
import rising.bot.domain.MainCharacter
import java.util.concurrent.CountDownLatch

@Repository
class MainCharacterRepository(
    private val firebaseDatabase: FirebaseDatabase
) {
    private val db = FirebaseDatabase.getInstance().getReference("main_characters")

    fun save(mainCharacter: MainCharacter): String {
        val key = db.push().key!!
        db.child(key).setValueAsync(mainCharacter.copy(id = key))
        return key
    }

    fun findByName(name: String): MainCharacter? {
        var result: MainCharacter? = null
        val latch = CountDownLatch(1)

        db.orderByChild("name").equalTo(name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        result = child.getValue(MainCharacter::class.java)
                        break
                    }
                    latch.countDown()
                }
                override fun onCancelled(error: DatabaseError) {
                    latch.countDown()
                }
            })
        latch.await()
        return result
    }

    fun findAll(): List<MainCharacter> {
        val result = mutableListOf<MainCharacter>()
        val latch = CountDownLatch(1)
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    child.getValue(MainCharacter::class.java)?.let { result.add(it) }
                }
                latch.countDown()
            }
            override fun onCancelled(error: DatabaseError) {
                latch.countDown()
            }
        })
        latch.await()
        return result
    }
}
