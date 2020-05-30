package com.messio.demo

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfiguration(val appProperties: AppProperties){
    init {
        // firebase-adminsdk-xw79b@fir-54071.iam.gserviceaccount.com
        val serviceAccount = FileInputStream("c:/Users/jpc/fir-54071-b2bee99c79a8.json")
        val options: FirebaseOptions = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fir-54071.firebaseio.com")
                .build()
        FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
}