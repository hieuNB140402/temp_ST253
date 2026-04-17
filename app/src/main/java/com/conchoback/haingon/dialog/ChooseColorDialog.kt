package com.conchoback.haingon.dialog

import android.content.Context
import android.graphics.Color
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseDialog
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.databinding.DialogColorPickerBinding
import kotlin.apply

class ChooseColorDialog(context: Context) : BaseDialog<DialogColorPickerBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_color_picker
    override val isCancelOnTouchOutside: Boolean = false
    override val isCancelableByBack: Boolean = false

    var onDoneEvent: ((Int) -> Unit) = {}
    var onCloseEvent: (() -> Unit) = {}
    var onDismissEvent: (() -> Unit) = {}
    private var color = Color.WHITE

    override fun initView() {
        binding.apply {
            colorPickerView.apply {
                hueSliderView = hueSlider
            }
        }
    }

    override fun initAction() {
        binding.apply {
            colorPickerView.setOnColorChangedListener { color = it }
            btnClose.tap {
                onCloseEvent.invoke()
                dismissDialog()
            }
            btnDone.tap {
                onDoneEvent.invoke(color)
                dismissDialog()
            }
        }
    }

    override fun onDismissListener() {
        onDismissEvent.invoke()
    }

}