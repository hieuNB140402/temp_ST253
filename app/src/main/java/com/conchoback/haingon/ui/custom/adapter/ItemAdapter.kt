package com.conchoback.haingon.ui.custom.adapter

import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.databinding.ItemStickerBinding

class ItemAdapter : BaseAdapter<String, ItemStickerBinding>(ItemStickerBinding::inflate) {
    var onItemClick : ((path: String, position: Int) -> Unit) = {_,_ ->}

    override fun onBind(binding: ItemStickerBinding, item: String, position: Int) {
        binding.apply {
            loadImage(item, imvSticker)
            root.tap { onItemClick.invoke(item, position) }
        }
    }
}