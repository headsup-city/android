package com.krish.headsup.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentGreetingsBinding
import com.krish.headsup.utils.makeStatusBarTranslucent
import com.krish.headsup.utils.restoreStatusBar
import com.krish.headsup.viewmodel.GreetingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GreetingsFragment : Fragment() {

    private var _binding: FragmentGreetingsBinding? = null
    private val binding get() = _binding!!

    private val greetingsViewModel: GreetingsViewModel by viewModels()

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGreetingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this@GreetingsFragment)

        configureGoogleSignIn()

        greetingsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        greetingsViewModel.apiError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                // Show a toast if there is an error
                Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show()
            }
        }

        with(binding) {
            buttonRegister.setOnClickListener {
                navController.navigate(R.id.action_greetingsFragment_to_registerFragment)
            }

            buttonLogin.setOnClickListener {
                navController.navigate(R.id.action_greetingsFragment_to_loginFragment)
            }

            buttonGoogleSignIn.setOnClickListener {
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            customizeSignInButton()
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1041086999375-et38shcavu4egonqku4b7dkpu06mbd9t.apps.googleusercontent.com")
            .requestEmail()
            .build()

        activity?.let {
            mGoogleSignInClient = GoogleSignIn.getClient(it, gso)
        }
    }

    private fun customizeSignInButton() {
        with(binding.buttonGoogleSignIn) {
            setSize(SignInButton.SIZE_WIDE)
            setColorScheme(SignInButton.COLOR_LIGHT)

            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                if (childView is TextView) {
                    childView.text = getString(R.string.sign_in_with_google)
                    childView.textSize = 18f
                    childView.setTextColor(Color.BLACK)
                    childView.typeface = Typeface.DEFAULT_BOLD
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        makeStatusBarTranslucent()
    }

    override fun onPause() {
        super.onPause()
        restoreStatusBar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                        Toast.makeText(context, "Sign in was cancelled", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(context, "Sign in failed", Toast.LENGTH_LONG).show()
                        Log.d(
                            "DebugSelf",
                            "message: " +
                                e.message.toString() +
                                ", statusCode: " +
                                e.statusCode.toString() +
                                ", localizedMessage: " +
                                e.localizedMessage.toString() +
                                ", cause: " +
                                e.cause.toString() +
                                ", status: " +
                                e.status.toString() +
                                ", stackTrace: " +
                                e.stackTrace.toString()
                        )
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {

        val accessToken = acct?.idToken
        Log.d("DebugSelf", "accessToken: " + accessToken.toString() + ", requestedScopes: " + acct?.requestedScopes.toString() + ", isExpired: " + acct?.isExpired.toString())

        if (accessToken != null) {
            greetingsViewModel.signInWithGoogle(accessToken)
        } else {
            Toast.makeText(context, "Couldn't sign in with google", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
