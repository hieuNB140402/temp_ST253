package com.conchoback.haingon.dialog

import android.app.Activity
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseDialog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.strings
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.databinding.DialogConfirmBinding

class ConfirmDialog(val context: Activity, val title: Int, val description: Int, val isError: Boolean = false) :
    BaseDialog<DialogConfirmBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_confirm
    override val isCancelOnTouchOutside: Boolean
        get() = false
    override val isCancelableByBack: Boolean
        get() = false


    var onNoClick: (() -> Unit) = {}
    var onYesClick: (() -> Unit) = {}
    override fun initView() {
        initText()
        initLayout()
    }

    override fun initAction() {
        binding.apply {
            btnLeft.tap {
                onYesClick.invoke()
                dismissDialog()
            }
            btnRight.tap {
                onNoClick.invoke()
                dismissDialog()
            }
        }
    }

    override fun onDismissListener() {}

    private fun initText() {
        binding.apply {
            tvTitle.text = context.strings(title)
            tvDescription.text = context.strings(description)
        }
    }

    private fun initLayout() {
        binding.apply {
            btnLeft.text = context.strings(R.string.no)
            btnLeft.text = context.strings(R.string.yes)

            if (isError) {
                btnLeft.gone()
            }
        }
    }
}