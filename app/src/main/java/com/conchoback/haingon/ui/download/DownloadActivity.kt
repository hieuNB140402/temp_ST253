package com.conchoback.haingon.ui.download

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.offline.Download
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.checkInternet
import com.conchoback.haingon.core.extension.launchIO
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.startIntentRightToLeft
import com.conchoback.haingon.core.extension.startIntentWithClearTop
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.databinding.ActivityDownloadBinding
import com.conchoback.haingon.ui.home.HomeActivity
import com.conchoback.haingon.ui.how_to_use.HowToUseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DownloadActivity : BaseActivity<ActivityDownloadBinding>() {
    private val viewModel: DownloadViewModel by viewModels()
    private val downloadAdapter by lazy { DownloadAdapter(this) }

    override fun setViewBinding(): ActivityDownloadBinding {
        return ActivityDownloadBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        initRcv()
        viewModel.setJsonList(intent.getStringExtra(IntentKey.INTENT_KEY) ?: "")
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.jsonList.collect { json -> setupUI(json) } }
        }
    }

    override fun viewListener() {
        binding.actionBar.apply {
            btnActionBarLeft.tap { startIntentWithClearTop(HomeActivity::class.java) }
            btnActionBarRight.tap { startIntentRightToLeft(HowToUseActivity::class.java) }
        }
    }


    // Init
    //==================================================================================================================
    override fun initActionBar() = with(binding.actionBar) {
        setImageActionBar(btnActionBarLeft, R.drawable.ic_home_htu)
        setImageActionBar(btnActionBarRight, R.drawable.ic_htu)
    }

    private fun initRcv() = with(binding.rcvDownload) {
        adapter = downloadAdapter
        itemAnimator = null
    }


    // Handle
    //==================================================================================================================
    private fun handleDownload(model: DownloadModel) {
        launchIO(
            blockIO = { viewModel.handleDownload(this@DownloadActivity, model) },
            blockMain = { isSuccess ->
                dismissLoading(true)

                val toastResult = if (isSuccess) {
                    getString(R.string.download_success)
                } else {
                    getString(R.string.an_error_occurred_please_try_again_later)
                }

                showToast(toastResult)
            }
        )
    }

    // Observable
    //==================================================================================================================
    private fun setupUI(json: String) {
        if (json == "") return

        launchIO(
            blockIO = { viewModel.convertFromJson() },
            blockMain = { list -> downloadAdapter.submitList(list) }
        )

        downloadAdapter.onDownloadClick = { model -> checkInternet { handleDownload(model) } }
    }

    // Result + Permission
    //==================================================================================================================
    @SuppressLint("GestureBackNavigation", "MissingSuperCall")
    override fun onBackPressed() {}

    // Ads
    //==================================================================================================================

}