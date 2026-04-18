package com.conchoback.haingon.ui.choose_clothes_after.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class ClothesAdapter : BaseAdapter<SelectedModel, ItemChooseClothesAccessoryBinding>(ItemChooseClothesAccessoryBinding::inflate) {
    var onItemClick: ((path: String, position: Int) -> Unit) = { _, _ -> }

    override fun onBind(binding: ItemChooseClothesAccessoryBinding, item: SelectedModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected
            val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
            loadImage("$domain${DomainKey.SUB_DOMAIN}/${item.path}", imvImage)

            root.tap {
                selectItem(position)
                onItemClick.invoke(item.path, position)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectItem(position: Int) {
        items.forEachIndexed { index, model ->
            model.isSelected = index == position
        }

        notifyDataSetChanged()
    }
}