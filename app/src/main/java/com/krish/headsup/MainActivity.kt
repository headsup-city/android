package com.krish.headsup

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.AuthStateChangeListener
import com.krish.headsup.utils.TokenManager
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.ionicons.Ionicons
import com.mikepenz.iconics.utils.sizeDp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AuthStateChangeListener {

    private lateinit var navController: NavController
    private lateinit var authState: AuthState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check the current authentication state
        authState = getAuthState()

        val authNavHostFragment = supportFragmentManager.findFragmentById(R.id.authNavigation) as NavHostFragment
        val authNavController = authNavHostFragment.navController
        val loadingFragmentContainer = findViewById<FrameLayout>(R.id.loadingFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        when (authState) {
            AuthState.NO_USER -> {
                authNavController.navigate(R.id.action_global_greetingFragment) // Navigate to loginFragment
                loadingFragmentContainer.visibility = View.GONE
                bottomNavigationView.visibility = View.GONE
            }
            AuthState.LOADING -> {
                loadingFragmentContainer.visibility = View.VISIBLE
                bottomNavigationView.visibility = View.GONE
            }
            AuthState.AUTHENTICATED -> {
                loadingFragmentContainer.visibility = View.GONE
                bottomNavigationView.visibility = View.VISIBLE

                setupBottomNavigationView()
                // Set up the navigation with the bottom navigation view
                bottomNavigationView.setupWithNavController(navController)
            }
        }
    }

    private fun getAuthState(): AuthState {
        val tokenManager = TokenManager(this)
        val accessToken = tokenManager.getTokenStore()?.access
        val refreshToken = tokenManager.getTokenStore()?.refresh

        return if (accessToken != null) {
            AuthState.AUTHENTICATED
        } else {
            AuthState.NO_USER
        }
    }

    private fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Connect the NavController with the BottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        setupIcons(bottomNavigationView)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.conversationFragment,
                R.id.profileFragment,
                R.id.textPostFragment,
                R.id.imagePostFragment,
                R.id.videoPostFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }
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

    override fun onAuthStateChanged(authState: AuthState) {
        this.authState = authState
        when (authState) {
            AuthState.AUTHENTICATED -> {
                // TODO: Perform necessary actions to initialize the authenticated state
            }
            AuthState.NO_USER -> setContentView(R.layout.fragment_greetings)
            AuthState.LOADING -> setContentView(R.layout.fragment_loading)
        }
    }
}
