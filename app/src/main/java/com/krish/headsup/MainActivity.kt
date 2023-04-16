package com.krish.headsup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.viewmodel.HomeViewModel
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.ionicons.Ionicons
import com.mikepenz.iconics.utils.sizeDp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authManager: AuthManager by lazy { (application as MyApplication).authManager }
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showStatusBar()

        updateAuthStateFromToken()

        val authNavHostFragment =
            supportFragmentManager.findFragmentById(R.id.authNavigation) as NavHostFragment
        val authNavController = authNavHostFragment.navController
        val mainContainer = findViewById<FragmentContainerView>(R.id.mainContainer)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        authManager.authState.observe(this) { authState ->
            when (authState) {
                AuthState.NO_USER -> {
                    authNavController.navigate(R.id.action_global_greetingFragment)
                    authNavHostFragment.view?.visibility = View.VISIBLE
                    mainContainer.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                AuthState.LOADING -> {
                    authNavHostFragment.view?.visibility = View.GONE
                    mainContainer.visibility = View.GONE
                    bottomNavigationView.visibility = View.GONE
                }
                AuthState.AUTHENTICATED -> {
                    authNavHostFragment.view?.visibility = View.GONE
                    mainContainer.visibility = View.VISIBLE
                    bottomNavigationView.visibility = View.VISIBLE

                    setupBottomNavigationView(bottomNavigationView, mainContainer)
                }
            }
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentNavController = mainContainer.getTag(R.id.mainContainer) as NavController
                if (!currentNavController.navigateUp()) {
                    if (this.isEnabled) {
                        isEnabled = false
                        finish()
                    } else {
                        isEnabled = true
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun updateAuthStateFromToken() {
        val tokenManager = TokenManager(this)
        val accessToken = tokenManager.getTokenStore()?.access

        val newState = if (accessToken != null) {
            AuthState.AUTHENTICATED
        } else {
            AuthState.NO_USER
        }

        authManager.updateAuthState(newState)
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

    private fun setupBottomNavigationView(bottomNavigationView: BottomNavigationView, mainContainer: FragmentContainerView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            val navGraphId = when (item.itemId) {
                R.id.homeTab -> R.navigation.home_navigation
                R.id.searchTab -> R.navigation.search_navigation
                R.id.createPostTab -> R.navigation.create_post_navigation
                R.id.activityTab -> R.navigation.activity_navigation
                R.id.profileTab -> R.navigation.profile_navigation
                else -> return@setOnItemSelectedListener false
            }

            val inflater = NavHostFragment.create(navGraphId)
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, inflater)
                .commitNow()

            val navController = inflater.navController
            mainContainer.setTag(R.id.mainContainer, navController)

            // Force status bar to be visible
            showStatusBar()

            setupDestinationChangeListener(navController, bottomNavigationView)

            true
        }

        // Set default tab
        bottomNavigationView.selectedItemId = R.id.homeTab
        setupIcons(bottomNavigationView)
    }

    private fun handleBottomNavigationViewVisibility(
        destinationId: Int,
        bottomNavigationView: BottomNavigationView
    ) {
        when (destinationId) {
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

    private fun setupDestinationChangeListener(
        navController: NavController,
        bottomNavigationView: BottomNavigationView
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleBottomNavigationViewVisibility(destination.id, bottomNavigationView)
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
