package com.krish.headsup

import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.managers.SelfDataManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.viewmodel.SharedViewModel
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.ionicons.Ionicons
import com.mikepenz.iconics.utils.sizeDp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authManager: AuthManager by lazy { (application as MyApplication).authManager }
    @Inject
    lateinit var tokenManager: TokenManager
    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private val navHostIds = listOf(
        R.navigation.home_navigation,
        R.navigation.search_navigation,
        R.navigation.create_post_navigation,
        R.navigation.activity_navigation,
        R.navigation.profile_navigation
    )

    private val navHostFragments = SparseArray<NavHostFragment>()

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


                    // Fetch user data
                    val accessToken = tokenManager.getTokenStore()?.access?.token
                    accessToken?.let { sharedViewModel.fetchUserData(it) }
                }
            }
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var navigatedUp = false
                for (i in 0 until navHostFragments.size()) {
                    val navController = navHostFragments.valueAt(i).navController
                    if (navController.navigateUp()) {
                        navigatedUp = true
                        break
                    }
                }
                if (!navigatedUp) {
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
        val fragmentManager = supportFragmentManager

        // Create a NavHostFragment for each navigation graph
        navHostIds.forEachIndexed { index, navGraphId ->
            val navHostFragment = NavHostFragment.create(navGraphId)
            navHostFragments.put(index, navHostFragment)
            fragmentManager.beginTransaction()
                .add(R.id.mainContainer, navHostFragment)
                .hide(navHostFragment)
                .commitNow()
            // Attach destination change listener to each NavController
            setupDestinationChangeListener(navHostFragment.navController, bottomNavigationView)
        }

        // Set default tab
        fragmentManager.beginTransaction().show(navHostFragments[0]).commitNow()
        mainContainer.setTag(R.id.mainContainer, navHostFragments[0].navController) // Set the initial NavController tag

        bottomNavigationView.setOnItemSelectedListener { item ->
            val index = when (item.itemId) {
                R.id.homeTab -> 0
                R.id.searchTab -> 1
                R.id.createPostTab -> 2
                R.id.activityTab -> 3
                R.id.profileTab -> 4
                else -> return@setOnItemSelectedListener false
            }

            val fragmentManager = supportFragmentManager
            val currentNavHostFragment = fragmentManager.primaryNavigationFragment as NavHostFragment

            fragmentManager.commit {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                hide(currentNavHostFragment)
                show(navHostFragments[index])
            }

            // Set the selected NavHostFragment as the primary navigation fragment
            switchNavHostFragment(fragmentManager, navHostFragments, index)

            val newNavController = navHostFragments[index].navController
            setupDestinationChangeListener(newNavController, bottomNavigationView)

            // Force status bar to be visible
            showStatusBar()

            true
        }

        // Set up back stack listener
        fragmentManager.addOnBackStackChangedListener {
            val currentNavController = fragmentManager.primaryNavigationFragment as NavHostFragment
            currentNavController.navController.apply {
                bottomNavigationView.selectedItemId = when (graph.id) {
                    R.id.home_navigation -> R.id.homeTab
                    R.id.search_navigation -> R.id.searchTab
                    R.id.create_post_navigation -> R.id.createPostTab
                    R.id.activity_navigation -> R.id.activityTab
                    R.id.profile_navigation -> R.id.profileTab
                    else -> R.id.homeTab
                }
            }
        }

        setupIcons(bottomNavigationView)
    }

    private fun switchNavHostFragment(fragmentManager: FragmentManager, navHostFragments: SparseArray<NavHostFragment>, index: Int) {
        fragmentManager.beginTransaction().apply {
            for (i in 0 until navHostFragments.size()) {
                val fragment = navHostFragments.valueAt(i)
                if (i == index) {
                    show(fragment)
                } else {
                    hide(fragment)
                }
            }
            commitNow()
        }
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
