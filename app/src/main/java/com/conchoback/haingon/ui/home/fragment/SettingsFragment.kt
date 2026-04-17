package com.conchoback.haingon.ui.home.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseFragment
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.policy
import com.conchoback.haingon.core.extension.shareApp
import com.conchoback.haingon.core.extension.startIntentRightToLeft
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.core.helper.RateHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.state.RateState
import com.conchoback.haingon.databinding.FragmentSettingsBinding
import com.conchoback.haingon.ui.language.LanguageActivity

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater)
    }

    override fun initView() {
        initRate()
    }

    override fun viewListener() {
        binding.apply {
            btnLanguage.tap { requireActivity().startIntentRightToLeft(LanguageActivity::class.java, IntentKey.INTENT_KEY) }
            btnShare.tap(1500) { requireActivity().shareApp() }
            btnRate.tap { handleRate() }
            btnPolicy.tap(1500) { requireActivity().policy() }
        }
    }


    // Init
    //==================================================================================================================
    private fun initRate() {
        binding.btnRate.isVisible = !sharePreference.getIsRate(requireActivity())
    }

    // Handle
    //==================================================================================================================
    private fun handleRate() {
        RateHelper.showRateDialog(requireActivity(), sharePreference) { state ->
            if (state != RateState.CANCEL) {
                binding.btnRate.gone()
                Toast.makeText(requireActivity(), R.string.have_rated, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateText() {
        val context = requireActivity()
        LanguageHelper.setLocale(context)
        binding.apply {
            tvTitle.text = context.strings(R.string.settings)
            tv1.text = context.strings(R.string.language)
            tv2.text = context.strings(R.string.rate)
            tv3.text = context.strings(R.string.share)
            tv4.text = context.strings(R.string.privacy_policy)
        }
    }

    // Observable
    //==================================================================================================================

    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================

    override fun onResume() {
        super.onResume()
        updateText()
    }
}