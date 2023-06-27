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
class PrivacyFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val privacyPolicy = view.findViewById<TextView>(R.id.privacy_policy)
        val backButton = view.findViewById<AppCompatImageButton>(R.id.backButton)

        val navController = NavHostFragment.findNavController(this)

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        val privacyPolicyText = """
    |Last Updated: June 27, 2023
    |
    |This Privacy Policy describes how the HeadsUp City mobile application ("HeadsUp City" or "App"), created and owned by Krishna Kant ("We", "Us", "Our"), collects, uses, and shares information about you.
    |
    |By using HeadsUp City, you consent to the collection, use, and disclosure of your information as described in this Privacy Policy.
    |
    |1. Information Collection
    |• Account Information: When you create an account, we may collect your name, email address, and other information you provide.
    |• User Content: We collect the content you post on HeadsUp City, including photos, comments, and other materials.
    |• Location Data: To enable the functionality of the App, we collect and process your location data. You can control this via your device settings.
    |• Usage Data: We may automatically collect data about how you interact with the App, such as what content you view, your search queries, and other app activities.
    |• Device Information: We may collect information about the device you are using, including the type of device, operating system, and device settings.
    |
    |2. Use of Information
    |• We may use the information we collect for various purposes, including to:
    |• Provide, maintain, and improve the App.
    |• Customize the content you see.
    |• Communicate with you about updates and promotions.
    |• Monitor and analyze usage trends.
    |• Detect, investigate, and prevent fraudulent transactions and other illegal activities.
    |
    |3. Sharing of Information
    |• We may share your information with third parties under the following circumstances:
    |• With your consent or at your direction.
    |• With third-party service providers who perform services on our behalf.
    |• To comply with legal obligations or protect our rights and those of our users.
    |
    |4. Third-Party Services
    |• The App may contain links to third-party websites or services. This Privacy Policy does not apply to the practices of third parties that we do not own or control.
    |
    |5. Data Security
    |• We implement measures aimed at securing your personal information. However, no security measure is 100% secure, and we cannot ensure the security of the information you transmit to HeadsUp City.
    |
    |6. International Data Transfers
    |• Your information may be transferred to and processed in countries other than the country you reside in.
    |
    |7. Children's Privacy
    |• HeadsUp City is not intended for individuals under the age of 13. We do not knowingly collect personal information from children under 13.
    |
    |8. Changes to this Privacy Policy
    |• We may update this Privacy Policy from time to time. We encourage you to review this Privacy Policy periodically to be informed of how we use your information.
    |
    |9. Contact Us
    |• If you have questions about this Privacy Policy, please contact Krishna Kant at support@headsup.city.
""".trimMargin()

        val formattedPrivacyPolicyText = formatText(privacyPolicyText)
        privacyPolicy.text = formattedPrivacyPolicyText
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
