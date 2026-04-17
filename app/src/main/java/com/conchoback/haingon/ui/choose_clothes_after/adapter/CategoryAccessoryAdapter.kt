package com.conchoback.haingon.ui.choose_clothes_after.adapter

import androidx.core.view.isVisible
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.conchoback.haingon.databinding.ItemCategoryAccessoryBinding

class CategoryAccessoryAdapter :
    BaseAdapter<AccessorySelectedModel, ItemCategoryAccessoryBinding>(ItemCategoryAccessoryBinding::inflate) {
    var onItemClick: ((position: Int) -> Unit) = { _ -> }

    override fun onBind(binding: ItemCategoryAccessoryBinding, item: AccessorySelectedModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected

            val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
            val path = "$domain/${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_2D}/${item.thumbnail}.png"

            loadImage(path, imvImage)

            root.tap { onItemClick.invoke(position) }
        }
    }
}