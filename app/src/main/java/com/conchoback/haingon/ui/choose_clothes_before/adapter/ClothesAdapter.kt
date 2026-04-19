package com.conchoback.haingon.ui.choose_clothes_before.adapter

import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.domain
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
                domain(DomainKey.BASE_PATH)
            }else{
                AssetsKey.ASSET_MANAGER
            }

            val fullPath = "$domain/$item"

            dLog("fullPath[$position]: $fullPath")

            loadImage(fullPath, imvImage)

            root.tap { onItemClick.invoke(item) }
        }
    }
}