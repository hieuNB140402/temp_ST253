package com.conchoback.haingon.ui.choose_clothes_after.adapter

import androidx.core.view.isVisible
import com.conchoback.haingon.R
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.SubAccessoryModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class SubAccessoryAdapter :
    BaseAdapter<SubAccessoryModel, ItemChooseClothesAccessoryBinding>(ItemChooseClothesAccessoryBinding::inflate) {
    var onItemClick: ((model: AccessoryModel, position: Int) -> Unit) = { _,_ -> }

    override fun onBind(binding: ItemChooseClothesAccessoryBinding, item: SubAccessoryModel, position: Int) {
        binding.apply {
            vFocus.isVisible = item.isSelected

            if (position != 0) {
                val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
                val path = "$domain${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_2D}/${item.accessory.value}.png"

                dLog("path[$position]: $path")
                loadImage(path, imvImage)
            } else {
                loadImage(R.drawable.ic_none_accessory, imvImage)
            }

            root.tap { onItemClick.invoke(item.accessory, position) }
        }
    }
}