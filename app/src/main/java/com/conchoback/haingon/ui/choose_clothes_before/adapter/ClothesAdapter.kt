package com.conchoback.haingon.ui.choose_clothes_before.adapter

import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class ClothesAdapter :
    BaseAdapter<String, ItemChooseClothesAccessoryBinding>(ItemChooseClothesAccessoryBinding::inflate) {
    var onItemClick: ((path: String) -> Unit) = { _ -> }

    override fun onBind(binding: ItemChooseClothesAccessoryBinding, item: String, position: Int) {
        binding.apply {
            vFocus.gone()

            val domain = if (item.contains(DomainKey.SPECIAL_CATEGORY)){
                val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
                "$domain${DomainKey.SUB_DOMAIN}"
            }else{
                AssetsKey.ASSET_MANAGER
            }
            val path = "$domain/$item"

            dLog("path[$position]: $path")

            loadImage(path, imvImage)

            root.tap { onItemClick.invoke(item) }
        }
    }
}