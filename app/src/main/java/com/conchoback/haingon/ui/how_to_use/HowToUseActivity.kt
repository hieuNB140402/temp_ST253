package com.conchoback.haingon.ui.how_to_use

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.invisible
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.databinding.ActivityHowToUseBinding
import com.conchoback.haingon.databinding.ActivityIntroBinding
import com.conchoback.haingon.ui.home.HomeActivity
import com.conchoback.haingon.ui.intro.IntroAdapter
import com.conchoback.haingon.ui.permission.PermissionActivity
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class HowToUseActivity : BaseActivity<ActivityHowToUseBinding>() {
    private val viewModel: HowToUseViewModel by viewModels()
    private val howToUseAdapter by lazy { HowToUseAdapter(this) }

    override fun setViewBinding(): ActivityHowToUseBinding {
        return ActivityHowToUseBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initVpg()
    }

    override fun dataObservable() {
        lifecycleScope.launch { viewModel.positionSelected.collect { position -> setupUiSelected(position) } }
    }

    override fun viewListener() = with(binding) {
        btnNext.setOnClickListener { handleNext() }
        btnPre.setOnClickListener { handlePre() }
        btnClose.tap { handleBackLeftToRight() }
        setupActionVpg()
    }

    // Init
    //==================================================================================================================

    override fun initActionBar() {}

    override fun initText() {}

    private fun initVpg() {
        binding.apply {
            vpgUse.adapter = howToUseAdapter
            binding.dotsIndicator.attachTo(binding.vpgUse)
            howToUseAdapter.submitList(DataLocal.itemHowToUseList)
        }
    }

    // Handle
    //==================================================================================================================
    private fun handleNext() {
        viewModel.setNextPosition()
        setCurrentVpg()
    }

    private fun handlePre() {
        viewModel.setPrePosition()
        setCurrentVpg()
    }

    private fun setCurrentVpg() {
        binding.vpgUse.setCurrentItem(viewModel.positionSelected.value, true)
    }

    private fun setupActionVpg() {
        binding.vpgUse.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setPosition(position)
            }
        })
    }

    // dataObservable
    //==================================================================================================================
    private fun setupUiSelected(position: Int) = with(binding) {
        if (position == 0) {
            btnPre.invisible()
        } else {
            btnPre.visible()
        }

        if (position == DataLocal.itemHowToUseList.size - 1) {
            btnNext.invisible()
        } else {
            btnNext.visible()
        }
    }

    // Result + Permission
    //==================================================================================================================
    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        exitProcess(0)
    }

    // Ads
    //==================================================================================================================
    override fun initAds() {
//        Admob.getInstance().loadNativeAll(this, getString(R.string.native_all))
//
//        Admob.getInstance().loadNativeAd(
//            this,
//            getString(R.string.native_intro),
//            binding.nativeAds,
//            R.layout.ads_native_medium_btn_bottom_2
//        )
    }
}