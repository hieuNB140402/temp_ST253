package com.conchoback.haingon.ui.choose_clothes_before.adapter

import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.gone
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.databinding.ItemChooseClothesAccessoryBinding

class AccessoryAdapter :
    BaseAdapter<AccessoryModel, ItemChooseClothesAccessoryBinding>(ItemChooseClothesAccessoryBinding::inflate) {
    var onItemClick: ((model: AccessoryModel) -> Unit) = { _ -> }

    override fun onBind(binding: ItemChooseClothesAccessoryBinding, item: AccessoryModel, position: Int) {
        binding.apply {
            vFocus.gone()

            val pathURL = loadAccessory2DURL(item.value)

            dLog("path[$position]: $pathURL")
            loadImage(pathURL, imvImage)


            root.tap { onItemClick.invoke(item) }
        }
    }
}