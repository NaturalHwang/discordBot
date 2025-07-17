package rising.bot.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.springframework.stereotype.Repository
import rising.bot.domain.GameCharacter
import rising.bot.domain.MainCharacter
import java.util.concurrent.CountDownLatch

@Repository
class GameCharacterRepository(
    private val firebaseDatabase: FirebaseDatabase
) {
//    private val db = FirebaseDatabase.getInstance().getReference("characters")
    private fun db(guildId: String, channelId: String) =
        firebaseDatabase.getReference("channels")
            .child(guildId)
            .child(channelId)
            .child("characters")

//    fun save(character: GameCharacter) {
//        val key = character.id ?: db.push().key!!
//        db.child(key).setValueAsync(character.copy(id = key))
//    }

//    fun saveAll(list: List<GameCharacter>) {
//        list.forEach { save(it) }
//    }

//    fun findByMain(main: MainCharacter): List<GameCharacter> {
//        val result = mutableListOf<GameCharacter>()
//        val latch = CountDownLatch(1)
//
//        db.orderByChild("mainId").equalTo(main.id)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (child in snapshot.children) {
//                        val character = child.getValue(GameCharacter::class.java)
//                        if (character != null) result.add(character)
//                    }
//                    latch.countDown()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    latch.countDown()
//                }
//            })
//        latch.await()
//        return result
//    }
//
//    fun deleteAll(list: List<GameCharacter>) {
//        list.forEach { it.id?.let { db.child(it).removeValueAsync() } }
//    }
//
//    fun deleteByMain(main: MainCharacter) {
//        val latch = CountDownLatch(1)
//        db.orderByChild("mainId").equalTo(main.id)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (child in snapshot.children){
//                        child.ref.removeValueAsync()
//                    }
//                    latch.countDown()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    latch.countDown()
//                }
//            })
//        latch.await()
//    }

    fun save(guildId: String, channelId: String, character: GameCharacter) {
        val db = db(guildId, channelId)
        val key = character.name
        db.child(key).setValueAsync(character.copy(id = key))
    }

    fun saveAll(guildId: String, channelId: String, list: List<GameCharacter>) {
        list.forEach { save(guildId, channelId, it) }
    }

    fun findByMain(guildId: String, channelId: String, main: MainCharacter): List<GameCharacter> {
        val result = mutableListOf<GameCharacter>()
        val latch = CountDownLatch(1)
        db(guildId, channelId)
            .orderByChild("mainId").equalTo(main.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val character = child.getValue(GameCharacter::class.java)
                        if (character != null) result.add(character)
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

    fun deleteAll(guildId: String, channelId: String, list: List<GameCharacter>) {
        val db = db(guildId, channelId)
        list.forEach { it.id?.let { db.child(it).removeValueAsync() } }
    }

    fun deleteByMain(guildId: String, channelId: String, main: MainCharacter) {
        val latch = CountDownLatch(1)
        db(guildId, channelId)
            .orderByChild("mainId").equalTo(main.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValueAsync()
                    }
                    latch.countDown()
                }

                override fun onCancelled(error: DatabaseError) {
                    latch.countDown()
                }
            })
        latch.await()
    }
}