package com.conchoback.haingon.ui.language

import android.annotation.SuppressLint
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.select
import com.conchoback.haingon.core.extension.setTextActionBar
import com.conchoback.haingon.core.extension.startIntentRightToLeft
import com.conchoback.haingon.core.extension.startIntentWithClearTop
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.databinding.ActivityLanguageBinding
import com.conchoback.haingon.ui.home.HomeActivity
import com.conchoback.haingon.ui.intro.IntroActivity
import kotlinx.coroutines.launch
import kotlin.jvm.java
import kotlin.system.exitProcess

class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {
    private val viewModel: LanguageViewModel by viewModels()

    private val languageAdapter by lazy { LanguageAdapter(this) }

    override fun setViewBinding(): ActivityLanguageBinding {
        return ActivityLanguageBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initRcv()
        val intentValue = intent.getStringExtra(IntentKey.INTENT_KEY)
        viewModel.setFirstLanguage(intentValue == null)
        viewModel.loadLanguages(sharePreference.getPreLanguage())
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.isFirstLanguage.collect { isFirst -> setupUI(isFirst) } }
            launch { viewModel.languageList.collect { list -> languageAdapter.submitList(list) } }
            launch { viewModel.codeLang.collect { code -> setupSelectCode(code) } }
        }
    }

    override fun viewListener() {
        binding.apply {
            actionBar.btnActionBarLeft.tap { handleBackLeftToRight() }
            actionBar.btnActionBarRight.tap { handleDone() }
        }
        handleRcv()
    }

    // Init
    //==================================================================================================================
    override fun initActionBar() {
        binding.actionBar.apply {
            btnActionBarLeft.setImageResource(R.drawable.ic_back)
            btnActionBarRight.setImageResource(R.drawable.ic_done)

            setTextActionBar(tvCenter, strings(R.string.language))
        }
    }

    override fun initText() {
        binding.actionBar.tvCenter.select()
    }

    private fun initRcv() {
        binding.rcvLanguage.apply {
            adapter = languageAdapter
            itemAnimator = null
        }
    }

    // Handle
    //==================================================================================================================
    private fun handleRcv() {
        binding.apply {
            languageAdapter.onItemClick = { code ->
                actionBar.btnActionBarRight.visible()
                viewModel.selectLanguage(code)
            }
        }
    }

    private fun handleDone() {
        val code = viewModel.codeLang.value
        if (code.isEmpty()) {
            showToast(R.string.not_select_lang)
            return
        }
        sharePreference.setPreLanguage(code)

        if (viewModel.isFirstLanguage.value) {
            sharePreference.setIsFirstLang(false)
            startIntentRightToLeft(IntroActivity::class.java)
            finishAffinity()
        } else {
            startIntentWithClearTop(HomeActivity::class.java)
        }
    }

    // Observable
    //==================================================================================================================
    private fun setupUI(isFirst: Boolean) {
        binding.apply {
            if (isFirst) {
                actionBar.apply {
//                    tvStart.visible()
                }
            } else {
                actionBar.apply {
                    btnActionBarLeft.visible()
                    tvCenter.visible()
                }
            }
        }
    }

    private fun setupSelectCode(code: String) {
        if (code.isNotEmpty()) {
            binding.actionBar.btnActionBarRight.visible()
        }
    }

    // Result + Permission
    //==================================================================================================================

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        if (!viewModel.isFirstLanguage.value) {
            handleBackLeftToRight()
        } else {
            finishAffinity()
        }
    }

    // Ads
    //==================================================================================================================

    override fun initAds() {
//        Admob.getInstance().loadNativeAd(
//            this@LanguageActivity,
//            getString(R.string.native_language),
//            binding.nativeAds,
//            R.layout.ads_native_big_btn_top
//        )
    }
}