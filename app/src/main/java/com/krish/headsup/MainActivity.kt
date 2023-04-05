package com.krish.headsup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.ionicons.Ionicons
import com.mikepenz.iconics.utils.sizeDp

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        subscribeToTopic()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setupIcons(bottomNavigationView)
        setupNavigation(bottomNavigationView, navController)
    }

    private fun setupIcons(bottomNavigationView: BottomNavigationView) {
        // Set icons for BottomNavigationView menu items
        bottomNavigationView.menu.findItem(R.id.homeTab).icon = IconicsDrawable(this, FontAwesome.Icon.faw_home).apply {
            sizeDp = 24
        }
        bottomNavigationView.menu.findItem(R.id.searchTab).icon = IconicsDrawable(this, Ionicons.Icon.ion_ios_search).apply {
            sizeDp = 24
        }
        bottomNavigationView.menu.findItem(R.id.createPostTab).icon = IconicsDrawable(this, Ionicons.Icon.ion_android_add).apply {
            sizeDp = 24
        }
        bottomNavigationView.menu.findItem(R.id.activityTab).icon = IconicsDrawable(this, FontAwesome.Icon.faw_bell).apply {
            sizeDp = 24
        }
        bottomNavigationView.menu.findItem(R.id.profileTab).icon = IconicsDrawable(this, FontAwesome.Icon.faw_user).apply {
            sizeDp = 24
        }
    }

    private fun setupNavigation(bottomNavigationView: BottomNavigationView, navController: NavController) {
        // Connect the NavController with the BottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
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
