package com.conchoback.haingon.dialog.text

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.extension.visible
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.databinding.ItemTextColorBinding

class TextColorAdapter : BaseAdapter<SelectedModel, ItemTextColorBinding>(ItemTextColorBinding::inflate) {
    var onChooseColorClick: (() -> Unit) = {}
    var onTextColorClick: ((Int, Int) -> Unit) = { _, _ -> }

    private var currentSelected = 1


    override fun onBind(binding: ItemTextColorBinding, item: SelectedModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected

            if (position == 0) {
                imvColor.gone()
                btnAddColor.visible()

                root.tap { onChooseColorClick.invoke() }
            } else {
                imvColor.visible()
                btnAddColor.gone()
                imvColor.setBackgroundColor(item.color)

                root.tap { onTextColorClick.invoke(item.color, position) }
            }
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
        currentSelected = 1
        notifyDataSetChanged()
    }
}