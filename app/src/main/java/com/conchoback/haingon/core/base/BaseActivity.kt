package com.conchoback.haingon.core.base

import android.R.attr.text
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.conchoback.haingon.App
import com.conchoback.haingon.core.extension.handleBackLeftToRight
import com.conchoback.haingon.core.extension.hideNavigation
import com.conchoback.haingon.core.extension.hideNavigationFullScreen
import com.conchoback.haingon.core.helper.LanguageHelper
import com.conchoback.haingon.dialog.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    private var _binding: T? = null
    protected val binding: T
        get() = _binding ?: error("Binding is null")

    protected abstract fun setViewBinding(): T
    protected abstract fun initView()
    protected abstract fun viewListener()
    protected abstract fun initActionBar()

    open fun dataObservable() {}
    open fun initText() {}
    open fun initAds() {}

    protected val sharePreference = App.instant.sharePreference

    var toast: Toast? = null
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setLocale(this)

        _binding = setViewBinding()
        setContentView(binding.root)
        toast = Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT)
        setupUI()
    }


    private fun setupUI() {
        initView()
        initAds()
        dataObservable()
        viewListener()
        initActionBar()
        initText()
    }

    override fun onResume() {
        super.onResume()
        hideNavigationFullScreen(true)
    }

    @SuppressLint("MissingSuperCall", "GestureBackNavigation")
    override fun onBackPressed() {
        handleBackLeftToRight()
    }

    suspend fun showLoading() {
        withContext(Dispatchers.Main) {
            if (!isFinishing && !loadingDialog.isShowing) {
                loadingDialog.show()
            }
        }
    }

    suspend fun dismissLoading(isBlack: Boolean = false) {
        withContext(Dispatchers.Main) {
            if (!isFinishing && loadingDialog.isShowing) {
                loadingDialog.dismiss()
                hideNavigationFullScreen(isBlack)
            }
        }
    }

    fun showToast(message: Any) {
        toast?.cancel()

        val text = when (message) {
            is String -> message
            is Int -> getString(message)
            else -> ""
        }

        toast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)

        toast?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideNavigationFullScreen(true)
        }
    }
}
