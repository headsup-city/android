package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krish.headsup.R
import com.krish.headsup.model.Post

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasePostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
abstract class BasePostFragment : Fragment() {
    abstract fun setupPostContent(view: View, post: Post)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_base_post, container, false)

        // Retrieve the post object from the arguments
        val post: Post = arguments?.getParcelable("post") ?: return view

        // Set up common UI elements like header, author info, and comment button

        // Set up content specific to the post type
        setupPostContent(view, post)

        return view
    }
}
