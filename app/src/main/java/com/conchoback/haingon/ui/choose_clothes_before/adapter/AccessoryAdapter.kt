package com.conchoback.haingon.ui.choose_clothes_before.adapter

import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class AccessoryAdapter :
    BaseAdapter<AccessoryModel, ItemChooseClothesAccessoryBinding>(ItemChooseClothesAccessoryBinding::inflate) {
    var onItemClick: ((model: AccessoryModel) -> Unit) = { _ -> }

    override fun onBind(binding: ItemChooseClothesAccessoryBinding, item: AccessoryModel, position: Int) {
        binding.apply {
            vFocus.gone()
            dLog("${AssetsKey.ASSET_MANAGER}/$item")
            val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
            val path = "$domain/${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_2D}/${item.value}.png"

            dLog("path[$position]: $path")
            loadImage(path, imvImage)


            root.tap { onItemClick.invoke(item) }
        }
    }
}