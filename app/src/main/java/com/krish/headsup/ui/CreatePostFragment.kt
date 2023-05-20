package com.krish.headsup.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.krish.headsup.OnPostCreatedListener
import com.krish.headsup.R
import com.krish.headsup.viewmodel.CreatePostViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var listener: OnPostCreatedListener? = null
    private val viewModel: CreatePostViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Location permission is needed to post.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        // Set touch listener on parent layout
        view.setOnTouchListener { _, _ ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postEditText: EditText = view.findViewById(R.id.postEditText)
        val postButton: Button = view.findViewById(R.id.postButton)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val charCountTextView: TextView = view.findViewById(R.id.charCountTextView)

        postEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                charCountTextView.text = "${s.length}/250"
            }
            override fun afterTextChanged(s: Editable) {}
        })

        postButton.setOnClickListener {
            val postText = postEditText.text.toString()
            if (postText.isEmpty()) {
                Toast.makeText(context, "Please write something", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (postText.length > 250) {
                Toast.makeText(context, "Post too long! Please keep it under 250 characters.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("caption", postText)

            val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request location permissions
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return@setOnClickListener
            }

            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        data.addFormDataPart("longitude", location.longitude.toString()) // Adjust the form data part name
                        data.addFormDataPart("latitude", location.latitude.toString()) // Adjust the form data part name
                    }

                    // Disable the input and button while API is being called
                    postEditText.isEnabled = false
                    postButton.isEnabled = false

                    val requestBody = data.build() // Build the MultipartBody here
                    viewModel.createPrimaryPost(requestBody) // Pass the MultipartBody to the createPrimaryPost function
                }
                .addOnFailureListener {
                    // Handle location fetch error
                    Toast.makeText(context, "Failed to get location. Please try again.", Toast.LENGTH_SHORT).show()
                }
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                CreatePostViewModel.LoadingStatus.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                CreatePostViewModel.LoadingStatus.Success -> {
                    progressBar.visibility = View.GONE
                    postEditText.isEnabled = true
                    postButton.isEnabled = true
                    Toast.makeText(context, "Post successfully created!", Toast.LENGTH_SHORT).show()
                    listener?.onPostCreated()
                    postEditText.text.clear()
                }

                CreatePostViewModel.LoadingStatus.Error -> {
                    progressBar.visibility = View.GONE
                    postEditText.isEnabled = true
                    postButton.isEnabled = true
                    Toast.makeText(
                        context,
                        "Failed to create post. Please check your network connection and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostCreatedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnPostCreatedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
