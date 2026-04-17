package com.conchoback.haingon.dialog.text

import android.app.Activity
import android.graphics.Bitmap
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseDialog
import com.conchoback.haingon.core.extension.invisible
import com.conchoback.haingon.core.extension.setFont
import com.conchoback.haingon.core.extension.showKeyboard
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.helper.BitmapHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.databinding.DialogTextBinding
import com.conchoback.haingon.dialog.ChooseColorDialog

class TextDialog(val context: Activity) : BaseDialog<DialogTextBinding>(context, maxWidth = true, maxHeight = true) {
    override val layoutId: Int = R.layout.dialog_text
    override val isCancelOnTouchOutside: Boolean
        get() = false
    override val isCancelableByBack: Boolean
        get() = false

    private val textFontAdapter by lazy { TextFontAdapter() }
    private val textColorAdapter by lazy { TextColorAdapter() }

    var textFontList: ArrayList<SelectedModel> = arrayListOf()
    var textColorList: ArrayList<SelectedModel> = arrayListOf()

    var onDoneClick: ((Bitmap?) -> Unit) = {}

    override fun initView() {
        initRcv()
        coerceEditText()
    }

    override fun initAction() {
        binding.btnDone.tap { handleDone() }
        handleTextChange()
        handleRcv()
    }

    // Init
    //==================================================================================================================
    private fun initRcv() = with(binding) {
        rcvTextColor.apply {
            adapter = textColorAdapter
            itemAnimator = null
        }

        rcvFont.apply {
            adapter = textFontAdapter
            itemAnimator = null
        }

        textColorList.addAll(DataLocal.getTextColorDefault(context))
        textFontList.addAll(DataLocal.getTextFontDefault())

        updateTextColorSelected(1)
        updateTextFontSelected(0)
    }

    // Handle
    //==================================================================================================================
    private fun handleRcv() {
        textColorAdapter.apply {
            onTextColorClick = { color, position -> updateTextColorSelected(position) }
            onChooseColorClick = { handleChooseColor() }
        }
        textFontAdapter.onTextFontClick = { font, position -> updateTextFontSelected(position) }
    }

    private fun updateTextColorSelected(position: Int, color: Int = 0) {
        textColorList = textColorList.map { it.copy(isSelected = false) }.toCollection(ArrayList())
        textColorList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }

        textColorAdapter.submitList(textColorList)

        val finalColor = if (position != 0) {
            coerceEditText()
            textColorList[position].color
        } else {
            color
        }

        updateTextColor(finalColor)
    }

    private fun updateTextColor(color: Int){
        binding.tvGetText.setTextColor(color)
        binding.edtText.setTextColor(color)
    }
    private fun updateTextFontSelected(position: Int) {
        textFontList = textFontList.map { it.copy(isSelected = false) }.toCollection(ArrayList())
        textFontList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
        textFontAdapter.submitList(textFontList)

        binding.tvGetText.setFont(textFontList[position].color)
        binding.edtText.setFont(textFontList[position].color)
    }

    private fun handleTextChange() {
        binding.edtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvGetText.text = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.btnDone.isInvisible = p0.toString().trim() == ""
            }
        })
    }

    private fun handleChooseColor() {
        val chooseColorDialog = ChooseColorDialog(context)
        chooseColorDialog.show()

        chooseColorDialog.onDoneEvent = { color ->
            updateTextColorSelected(0, color)
            context.showKeyboard(binding.edtText)
        }
    }


    private fun coerceEditText() = with(binding){
        edtText.postDelayed({
            context.showKeyboard(edtText)
        }, 500)
    }

    fun handleDone() {
        binding.apply {
            edtText.clearFocus()
            edtText.invisible()
            tvGetText.isVisible = !TextUtils.isEmpty(edtText.text.toString().trim())
            val bitmap = BitmapHelper.getBitmapFromEditText(tvGetText)
            onDoneClick.invoke(bitmap)
            dismissDialog()
        }
    }

    // Result + Permission
    //==================================================================================================================
    override fun onDismissListener() {}


}