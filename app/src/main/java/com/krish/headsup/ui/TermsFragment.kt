package com.krish.headsup.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val terms = view.findViewById<TextView>(R.id.terms)
        val backButton = view.findViewById<AppCompatImageButton>(R.id.backButton)

        val navController = NavHostFragment.findNavController(this)

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        val termsText = """
            |Last Updated: June 27, 2023
            |
    |1. Acceptance of Terms
    |• By accessing and using the HeadsUp City mobile application, you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please refrain from using the app.
    |
    |2. Changes to the Terms
    |• We reserve the right to modify these Terms and Conditions at any time. Your continued usage of HeadsUp City will mean you accept those changes.
    |
    |3. User Responsibilities
    |• You agree not to use HeadsUp City for any purpose that is illegal or prohibited by these Terms. You are responsible for all content you share and interactions you have within the app.
    |
    |4. Content Ownership
    |• Content you share on HeadsUp City remains your property. However, by posting, you grant us a license to use, display, and distribute this content.
    |
    |5. Privacy
    |• Your privacy is important to us. Please review our Privacy Policy to understand our practices.
    |
    |6. Intellectual Property
    |• All intellectual property rights in the app and its content, features, and functionality are owned by Krishna Kant.
    |
    |7. Limitation of Liability
    |• Under no circumstances will Krishna Kant be liable for any direct, indirect, incidental, special, consequential, or exemplary damages arising out of or related to your use of the app.
    |
    |8. Indemnification
    |• You agree to indemnify and hold harmless Krishna Kant from and against all damages, losses, and expenses of any kind arising from or in connection with your use of HeadsUp City or your violation of these Terms.
    |
    |9. Termination
    |• We reserve the right to terminate your access to the app for any reason, including breach of these Terms.
    |
    |10. Governing Law
    |• These Terms shall be governed by and construed in accordance with the laws of the country where Krishna Kant resides, without regard to its conflict of law provisions.
    |
    |11. Contact
    |• For any questions regarding these Terms and Conditions, please contact Krishna Kant at support@headsup.city.
    """.trimMargin()

        val formattedTermsText = formatText(termsText)
        terms.text = formattedTermsText
    }

    private fun formatText(text: String): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        val headingPattern = Regex("""\d+\..*""")
        val bulletPattern = Regex("""\•.*""")
        text.lines().forEach { line ->
            if (headingPattern.matches(line)) {
                val start = builder.length
                builder.append(line)
                val end = builder.length
                builder.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } else if (bulletPattern.matches(line)) {
                val start = builder.length
                builder.append(line)
                val end = builder.length
                builder.setSpan(StyleSpan(Typeface.NORMAL), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            } else {
                builder.append(line)
            }
            builder.append("\n")
        }

        return builder
    }
}
