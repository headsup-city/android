package com.krish.headsup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToTopic()
    }

    private fun subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("your_topic")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully subscribed to the topic
                } else {
                    // Failed to subscribe to the topic
                }
            }
    }
}
