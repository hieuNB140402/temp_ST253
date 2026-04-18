package com.conchoback.haingon.ui.view3d

import android.content.Intent
import android.view.LayoutInflater
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.webkit.WebViewClientCompat
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseActivity
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.expandView
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.invisible
import com.conchoback.haingon.core.extension.margin
import com.conchoback.haingon.core.extension.startIntentRightToLeft
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.core.helper.InternetHelper
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.ClothesModel
import com.conchoback.haingon.databinding.ActivityView3dBinding
import com.conchoback.haingon.ui.custom.CustomActivity
import com.conchoback.haingon.ui.choose_clothes_after.ChooseClothesAccessoryActivity
import com.conchoback.haingon.ui.download.DownloadActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class View3dActivity : BaseActivity<ActivityView3dBinding>() {
    private val viewModel: View3dViewModel by viewModels()

    private val btnCharacterList by lazy {
        arrayListOf(
            binding.btnType1,
            binding.btnType2,
            binding.btnType3,
            binding.btnType4,
        )
    }

    private val resultClothesLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        viewModel.updateClothesEdit(result)
    }

    override fun setViewBinding(): ActivityView3dBinding {
        return ActivityView3dBinding.inflate(LayoutInflater.from(this))
    }

    override fun initView() {
        viewModel.dispatch(View3dAction.ChangeTypeCharacter(ValueKey.CHARACTER_1))
        initWebView()
        binding.lnlFeature.post { viewModel.updateWithLayoutFeature(binding.lnlFeature.height) }
    }

    override fun dataObservable() {
        lifecycleScope.launch {
            launch { viewModel.typeClothes.collect { setupTypeClothes(it) } }
            launch { viewModel.themeFlow.collect { applyTheme(it) } }
            launch { viewModel.typeCharacterFlow.collect { renderCharacter(it) } }
            launch { viewModel.shirtFlow.collect { renderShirt(it) } }
            launch { viewModel.pantFlow.collect { renderPant(it) } }
            launch { viewModel.accessoryFlow.collect { renderAccessories(it) } }

            launch { viewModel.isShowFeatureFlow.collect { renderIsShowFeature(it) } }
        }
    }

    override fun viewListener() {
        binding.apply {
            btnBack.tap { handleBackLeftToRight() }
            btnDownload.tap { handleDownload() }

            btnThemeDark.tap { viewModel.dispatch(View3dAction.ChangeTheme(ValueKey.DARK_THEME)) }
            btnThemeLight.tap { viewModel.dispatch(View3dAction.ChangeTheme(ValueKey.LIGHT_THEME)) }

            btnType1.tap { checkInternet { viewModel.dispatch(View3dAction.ChangeTypeCharacter(ValueKey.CHARACTER_1)) } }
            btnType2.tap { checkInternet { viewModel.dispatch(View3dAction.ChangeTypeCharacter(ValueKey.CHARACTER_2)) } }
            btnType3.tap { checkInternet { viewModel.dispatch(View3dAction.ChangeTypeCharacter(ValueKey.CHARACTER_3)) } }
            btnType4.tap { checkInternet { viewModel.dispatch(View3dAction.ChangeTypeCharacter(ValueKey.CHARACTER_4)) } }

            btnShirt.tap { handleTapClothes(ValueKey.SHIRT) }
            btnPant.tap { handleTapClothes(ValueKey.PANT) }
            btnAccessory.tap { handleTapClothes(ValueKey.ACCESSORY) }

            btnImage.tap { handleEditClothes(ValueKey.IMAGE_OPTION) }
            btnBrush.tap { handleEditClothes(ValueKey.BRUSH_OPTION) }
            btnSticker.tap { handleEditClothes(ValueKey.STICKER_OPTION) }
            btnEmoji.tap { handleEditClothes(ValueKey.EMOJI_OPTION) }
            btnText.tap { handleEditClothes(ValueKey.TEXT_OPTION) }

        btnClose.tap { viewModel.dispatch(View3dAction.ChangeShowFeature(false, "")) }
        }
    }

    // Init
    //==================================================================================================================
    override fun initActionBar() {}

    private fun initWebView() = with(binding) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return viewModel.loadWebView(this@View3dActivity, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                lifecycleScope.launch {
                    delay(100)
                    viewModel.dispatch(View3dAction.ChangeTheme(ValueKey.LIGHT_THEME))
//                    viewModel.dispatch(View3dAction.ChangeTypeClothes(ValueKey.ACCESSORY))
                    viewModel.dispatch(
                        View3dAction.ChangeTypeClothes(
                            intent.getStringExtra(IntentKey.CLOTHES_TYPE) ?: ValueKey.SHIRT
                        )
                    )

                }

            }
        }
        webView.loadUrl(ValueKey.HTML_LINK)
    }

    // Handle
    //==================================================================================================================
    private fun handleTapClothes(type: String) {
        if (!viewModel.isAccessory()) {
            viewModel.dispatch(View3dAction.ChangeShowFeature(true, type))
            return
        }

        checkInternet {
            val nextScreen = Intent(this, ChooseClothesAccessoryActivity::class.java)

            nextScreen.putExtra(IntentKey.PATH_KEY, viewModel.getClothesByType(type))
            nextScreen.putExtra(IntentKey.TYPE_KEY, type)

            resultClothesLauncher.launch(nextScreen)
        }

    }

    private fun handleEditClothes(typeEdit: Int) {
        val nextScreen = Intent(this, CustomActivity::class.java)
        val typeClothesSelected = viewModel.typeClothesSelected

        nextScreen.putExtra(IntentKey.IMAGE_KEY, viewModel.getClothesByType(typeClothesSelected))
        nextScreen.putExtra(IntentKey.TYPE_CLOTHES_KEY, typeClothesSelected)
        nextScreen.putExtra(IntentKey.INTENT_KEY, typeEdit)

        resultClothesLauncher.launch(nextScreen)
    }

    private fun checkInternet(action: () -> Unit) {
        if (InternetHelper.isInternetAvailable(this)) {
            action.invoke()
        } else {
            showToast(R.string.please_check_your_network_connection)
        }
    }

    private fun handleDownload() {
        lifecycleScope.launch(Dispatchers.IO) {
            showLoading()
            val downloadList = viewModel.getDownloadData(this@View3dActivity)
            val jsonList = Gson().toJson(downloadList)

            withContext(Dispatchers.Main) {
                dismissLoading()
                startIntentRightToLeft(DownloadActivity::class.java, jsonList)
                finish()
            }
        }
    }

    // Observable
    //==================================================================================================================
    private fun setupTypeClothes(type: String) = with(binding) {
        dLog("typeClothes: $type")
        val pathClothes = (intent.getStringExtra(IntentKey.PATH_KEY) ?: "hoicham")

        when (type) {
            ValueKey.SHIRT -> {
                btnShirt.visible()
                btnPant.gone()
                btnAccessory.gone()

                viewModel.dispatch(View3dAction.ChangeShirt(ClothesModel(ValueKey.SHIRT, pathClothes)))
            }

            ValueKey.PANT -> {
                btnShirt.gone()
                btnPant.visible()
                btnPant.margin("top", 0)
                btnAccessory.gone()

                viewModel.dispatch(View3dAction.ChangePant(ClothesModel(ValueKey.PANT, pathClothes)))
            }

            ValueKey.COMBO -> {
                btnShirt.visible()
                btnPant.visible()
                btnAccessory.gone()

                val (shirtPath, pantPath) = viewModel.loadComboPath(pathClothes)
                viewModel.apply {
                    dispatch(View3dAction.ChangeShirt(ClothesModel(ValueKey.SHIRT, shirtPath)))
                    dispatch(View3dAction.ChangePant(ClothesModel(ValueKey.PANT, pantPath)))
                }
            }

            ValueKey.ACCESSORY -> {
                btnShirt.visible()
                btnPant.visible()
                btnAccessory.visible()

                val pathClothesDefault = sharePreference.getFirstClothes()
                viewModel.apply {
                    dispatch(View3dAction.ChangeShirt(ClothesModel(ValueKey.SHIRT, pathClothesDefault)))
                    dispatch(View3dAction.ChangePant(ClothesModel(ValueKey.PANT, pathClothesDefault)))
                    lifecycleScope.launch {
                        delay(500)
                        dispatch(View3dAction.ChangeAccessory(viewModel.convertFromJsonAccessory(pathClothes)))
                    }
                }
            }

            else -> return@with
        }
    }

    private fun applyTheme(theme: String) = with(binding) {
        dLog("theme: $theme")
        if (theme == ValueKey.DARK_THEME) {
            btnThemeDark.setImageResource(R.drawable.ic_dark_selected)
            btnThemeLight.setImageResource(R.drawable.ic_light)
        } else {
            btnThemeDark.setImageResource(R.drawable.ic_dark)
            btnThemeLight.setImageResource(R.drawable.ic_light_selected)
        }
        webView.evaluateJavascript(viewModel.updateTheme(theme), null)
    }

    private fun renderCharacter(type: String) {
        dLog("renderCharacter: $type")
        val typeIndex = viewModel.getIndexCharacter(type)

        btnCharacterList.forEachIndexed { index, button ->
            val res = if (index == typeIndex) R.drawable.bg_1000_stroke_gray_gradient
            else R.drawable.bg_1000_solid_white
            button.setBackgroundResource(res)
        }

        binding.webView.evaluateJavascript(viewModel.updateCharacter(type), null)
    }

    private fun renderShirt(item: ClothesModel?) {
        if (item != null) {
            binding.webView.evaluateJavascript(viewModel.updateItem(item.typeClothes to item.item), null)
        }

        dLog("Shirt: $item")
    }

    private fun renderPant(item: ClothesModel?) {
        if (item != null) {
            binding.webView.evaluateJavascript(viewModel.updateItem(item.typeClothes to item.item), null)
        }
        dLog("Pant: $item")
    }

    private fun renderAccessories(list: List<AccessoryModel>) {
        binding.webView.evaluateJavascript(viewModel.updateAccessory(list), null)
        dLog("Accessories: $list")
    }

    private fun renderIsShowFeature(isShow: Boolean) = with(binding) {
        binding.lnlClothes.post {
            if (!isShow) {
                lnlFeature.invisible()
                lnlClothes.visible()
            } else {
                lnlFeature.expandView(lnlClothes.height, viewModel.withLayoutFeature)
            }
        }

    }

    // Result + Permission
    //==================================================================================================================

    // Ads
    //==================================================================================================================


}