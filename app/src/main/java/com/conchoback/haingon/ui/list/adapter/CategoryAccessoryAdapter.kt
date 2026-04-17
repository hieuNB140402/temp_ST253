package com.conchoback.haingon.ui.list.adapter

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.conchoback.haingon.databinding.ItemCategoryAccessoryBinding
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class CategoryAccessoryAdapter : BaseAdapter<AccessorySelectedModel, ItemCategoryAccessoryBinding>(ItemCategoryAccessoryBinding::inflate) {
    var onItemClick: ((position: Int) -> Unit) = { _ -> }

    override fun onBind(binding: ItemCategoryAccessoryBinding, item: AccessorySelectedModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected

            val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
            loadImage("$domain/${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_2D}/${item.thumbnail}", imvImage)

            root.tap {}
        }
    }
}