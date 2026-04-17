package com.conchoback.haingon.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.viewModel
import com.conchoback.haingon.core.utils.state.CallApiState
import com.conchoback.haingon.core.utils.state.HandleState
import com.conchoback.haingon.databinding.ActivitySplashBinding
import com.conchoback.haingon.ui.home.DataViewModel
import com.conchoback.haingon.ui.intro.IntroActivity
import com.conchoback.haingon.ui.language.LanguageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    var intentActivity: Intent? = null

    private val dataViewModel: DataViewModel by viewModels()

    override fun setViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        if (!isTaskRoot && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)) {
            finish(); return
        }

        intentActivity = Intent(this, if (sharePreference.getIsFirstLang()) LanguageActivity::class.java else IntroActivity::class.java)


        lifecycleScope.launch(Dispatchers.IO) {
            dataViewModel.checkCurrentVersion(this@SplashActivity)

            dataViewModel.getAllParts(this@SplashActivity, sharePreference).collect { state ->
                when (state) {
                    CallApiState.Loading -> {}
                    else -> {
                        withContext(Dispatchers.Main) {
                            startActivity(intentActivity)
                        }
                    }
                }
            }
        }
    }

    override fun viewListener() {}


    // Init
    //==================================================================================================================
    override fun initActionBar() {}

    // Handle
    //==================================================================================================================

    // Observable
    //==================================================================================================================

    // Result + Permission
    //==================================================================================================================
    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
    }

    // Ads
    //==================================================================================================================
}