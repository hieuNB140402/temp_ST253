package com.conchoback.haingon.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.rateApp
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.helper.InternetHelper
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.state.RateState
import com.conchoback.haingon.databinding.ActivityHomeBinding
import com.conchoback.haingon.ui.home.adapter.HomeAdapter
import com.conchoback.haingon.ui.home.view_model.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    private val viewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }

    val resultDelete = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { data ->
        viewModel.reSubmitMyCreation(data)
    }

    private val btnTabList by lazy {
        arrayListOf(
            binding.bottomNav.btnTabHome,
            binding.bottomNav.btnTabCreation,
            binding.bottomNav.btnTabSetting,
        )
    }

    private val imvTabList by lazy {
        arrayListOf(
            binding.bottomNav.imvTabHome,
            binding.bottomNav.imvTabCreation,
            binding.bottomNav.imvTabSetting,
        )
    }

    private val lineTabList by lazy {
        arrayListOf(
            binding.bottomNav.vLineTabHome,
            binding.bottomNav.vLineTabCreation,
            binding.bottomNav.vLineTabSetting,
        )
    }

    override fun setViewBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        sharePreference.setCountBack(sharePreference.getCountBack() + 1)
        initVpg()
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.currentTab.collect { index -> setupTab(index) } }
        }
    }

    override fun viewListener() {
        handleBottomNav()
    }

    // Init
    //==================================================================================================================
    private fun initVpg() {
        binding.vpgHome.apply {
            adapter = homeAdapter
            isUserInputEnabled = false
            setCurrentItem(0, true)
        }
    }

    override fun initActionBar() {}

    // Handle
    //==================================================================================================================
    private fun handleBottomNav() {
        btnTabList.forEachIndexed { indexButton, button ->
            button.tap { viewModel.setCurrentTab(indexButton) }
        }
    }

    // Observable
    //==================================================================================================================
    fun setupTab(index: Int) {
        if (index == -1) return
        binding.apply {
            vpgHome.setCurrentItem(index, true)

            lineTabList.forEachIndexed { indexLine, line -> line.isInvisible = indexLine != index }

            imvTabList.forEachIndexed { indexImage, image ->
                val res = if (indexImage == index)
                    DataLocal.bottomNavigationSelected[indexImage]
                else
                    DataLocal.bottomNavigationNotSelect[indexImage]
                image.setImageResource(res)
            }

            lifecycleScope.launch(Dispatchers.IO) {
                if (index == 1) {
                    viewModel.setSelectionState(false)
                    viewModel.getAllClothesSaved()
                }
            }
        }
    }

    // Result + Permission
    //==================================================================================================================
    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        if (!sharePreference.getIsRate(this) && sharePreference.getCountBack() % 2 == 0) {
            rateApp(sharePreference) { state ->
                if (state != RateState.CANCEL) {
                    showToast(R.string.have_rated)
                }
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        delay(1000)
                        exitProcess(0)
                    }
                }
            }
        } else {
            exitProcess(0)
        }
    }

    override fun onRestart() {
        super.onRestart()

        initNativeCollab()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) { viewModel.deleteCacheFolder(this@HomeActivity) }
    }

    // Ads
    //==================================================================================================================
    private fun initNativeCollab() {
//        Admob.getInstance().loadNativeCollapNotBanner(this, getString(R.string.native_collap_home), binding.flNativeCollab)
    }

    override fun initAds() {
//        Admob.getInstance().loadInterAll(this@HomeActivity, getString(R.string.inter_all))
//        Admob.getInstance().loadNativeAd(
//            this, getString(R.string.native_home),
//            binding.nativeAds,
//            R.layout.ads_native_medium_btn_bottom_2
//        )
//        initNativeCollab()
    }
}