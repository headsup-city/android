package com.krish.headsup
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.TokenManager
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.ionicons.Ionicons
import com.mikepenz.iconics.utils.sizeDp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authManager: AuthManager by lazy { (application as MyApplication).authManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showStatusBar()
        updateAuthStateFromToken()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        authManager.authState.observe(this) { authState ->
            when (authState) {
                AuthState.NO_USER -> {
                    navController.navigate(R.id.auth_navigation)
                    bottomNavigationView.visibility = View.GONE
                }

                AuthState.LOADING -> {
                    // ... loading state logic ...
                }

                AuthState.AUTHENTICATED -> {
                    navController.navigate(R.id.main_navigation)
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        bottomNavigationView.setupWithNavController(navController)

        setupIcons(bottomNavigationView)
    }

    private fun updateAuthStateFromToken() {
        val tokenManager = TokenManager(this)
        tokenManager.getTokenStore()?.access?.let {
            authManager.updateAuthState(AuthState.AUTHENTICATED)
        } ?: run {
            authManager.updateAuthState(AuthState.NO_USER)
        }
    }

    private fun showStatusBar() {
        // Force status bar to be visible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.apply {
                show(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
        }
    }

    private fun setupIcons(bottomNavigationView: BottomNavigationView) {
        // Set icons for BottomNavigationView menu items
        bottomNavigationView.menu.findItem(R.id.homeTab).icon =
            IconicsDrawable(this, FontAwesome.Icon.faw_home).apply {
                sizeDp = 24
            }
        bottomNavigationView.menu.findItem(R.id.searchTab).icon =
            IconicsDrawable(this, Ionicons.Icon.ion_ios_search).apply {
                sizeDp = 24
            }
        bottomNavigationView.menu.findItem(R.id.createPostTab).icon =
            IconicsDrawable(this, Ionicons.Icon.ion_android_add).apply {
                sizeDp = 24
            }
        bottomNavigationView.menu.findItem(R.id.activityTab).icon =
            IconicsDrawable(this, FontAwesome.Icon.faw_bell).apply {
                sizeDp = 24
            }
        bottomNavigationView.menu.findItem(R.id.profileTab).icon =
            IconicsDrawable(this, FontAwesome.Icon.faw_user).apply {
                sizeDp = 24
            }
    }
}
