package com.conchoback.haingon.dialog.text

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.setFont
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.databinding.ItemFontBinding

class TextFontAdapter() : BaseAdapter<SelectedModel, ItemFontBinding>(ItemFontBinding::inflate) {
    var onTextFontClick: ((Int, Int) -> Unit) = { _, _ -> }
    private var currentSelected = 0

    override fun onBind(binding: ItemFontBinding, item: SelectedModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected

            tvFont.setFont(item.color)
            root.tap { onTextFontClick.invoke(item.color, position) }
        }
    }

    fun submitItem(position: Int, list: ArrayList<SelectedModel>) {
        if (position != currentSelected) {
            items.clear()
            items.addAll(list)

            notifyItemChanged(currentSelected)
            notifyItemChanged(position)

            currentSelected = position
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitListReset(list: ArrayList<SelectedModel>){
        items.clear()
        items.addAll(list)
        currentSelected = 0
        notifyDataSetChanged()
    }
}