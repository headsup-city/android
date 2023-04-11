package com.krish.headsup.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the received message
        // You can parse the remoteMessage and create a notification, or update your app's UI
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save the new token to your server or app preferences
        sendRegistrationTokenToServer(token)
    }

    private fun sendRegistrationTokenToServer(token: String) {
        // Use your API service to send the token to your server
        // For example, using Retrofit
        CoroutineScope(Dispatchers.IO).launch {
            try {
            } catch (e: IOException) {
                // Handle network exceptions
            } catch (e: HttpException) {
                // Handle non-2xx HTTP responses
            } catch (e: Exception) {
                // Handle any other exceptions
            }
        }
    }
}
