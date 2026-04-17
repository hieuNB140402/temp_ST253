package com.conchoback.haingon.dialog

import android.app.Activity
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseDialog
import com.conchoback.haingon.databinding.DialogLoadingBinding

class LoadingDialog(val context: Activity) :
    BaseDialog<DialogLoadingBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_loading
    override val isCancelOnTouchOutside: Boolean
        get() = false
    override val isCancelableByBack: Boolean
        get() = false

    override fun initView() {}

    override fun initAction() {}

    override fun onDismissListener() {}
}