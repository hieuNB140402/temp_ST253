package com.conchoback.haingon.ui.preview

import android.R.attr.type
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.launchIO
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.setImageActionBar
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.databinding.ActivityPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {
    private val viewModel: PreviewViewModel by viewModels()

    override fun setViewBinding(): ActivityPreviewBinding {
        return ActivityPreviewBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.setClothesJson(intent.getStringExtra(IntentKey.INTENT_KEY) ?: "")
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.clothesJson.collect { json -> setupJson(json) } }
        }
    }

    override fun viewListener() {
        binding.actionBar.apply {
            btnActionBarLeft.tap { handleBackLeftToRight() }
            btnActionBarNextToRight.tap { handleDelete() }
            btnActionBarRight.tap { handleDownload() }
        }

    }

    // Init
    //==================================================================================================================
    override fun initActionBar() = with(binding.actionBar) {
        setImageActionBar(btnActionBarLeft, R.drawable.ic_back)
        setImageActionBar(btnActionBarNextToRight, R.drawable.ic_delete)
        setImageActionBar(btnActionBarRight, R.drawable.ic_download)
    }


    // Handle
    //==================================================================================================================
    private fun handleDelete() {
        launchIO(
            blockIO = { viewModel.deleteClothesSavedById() },
            blockMain = {
                withContext(Dispatchers.Main) {
                    val resultIntent = Intent()
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        )
    }

    private fun handleDownload() {
        launchIO(
            blockIO = {
                showLoading()
                viewModel.downloadClothesFileToExternal(this@PreviewActivity)
            },
            blockMain = { isSuccess ->
                dismissLoading(true)
                val toastRes = if (isSuccess) R.string.download_success else R.string.an_error_occurred_please_try_again_later
                showToast(toastRes)
            }
        )
    }

    // Observable
    //==================================================================================================================
    private fun setupJson(json: String) {
        if (json == "") return
        launchIO(
            blockIO = { viewModel.convertJson(this@PreviewActivity) },
            blockMain = {fullPathThumb, type, extension ->
                loadImage(fullPathThumb, binding.imvThumb)
                binding.tvTypeClothes.text = type
                binding.tvExtension.text = extension
            }
        )
    }

    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================

}