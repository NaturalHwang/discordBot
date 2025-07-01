package rising.bot.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig {
    @Bean
    fun firebaseApp(): FirebaseApp {
        val serviceAccount = FileInputStream("D:/project/key/risingbot-52a59-firebase-adminsdk-fbsvc-4380db6c68.json")
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl("https://risingbot-52a59-default-rtdb.firebaseio.com/")
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseDatabase(firebaseApp: FirebaseApp): FirebaseDatabase {
        return FirebaseDatabase.getInstance(firebaseApp)
    }
}