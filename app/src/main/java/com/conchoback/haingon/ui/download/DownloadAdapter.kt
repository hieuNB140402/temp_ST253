package com.conchoback.haingon.ui.download

import android.content.Context
import android.widget.ImageView
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.capitalizeFirst
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
import com.conchoback.haingon.core.helper.LoadClothesHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.databinding.ItemDownloadBinding
import java.io.File

class DownloadAdapter(val context: Context) : BaseAdapter<DownloadModel, ItemDownloadBinding>(ItemDownloadBinding::inflate) {
    var onDownloadClick: ((DownloadModel) -> Unit) = {}

    override fun onBind(binding: ItemDownloadBinding, item: DownloadModel, position: Int) {
        binding.apply {
            val (extension, type) = when (item.typeClothes) {
                ValueKey.SHIRT -> "PNG" to "Shirt"
                ValueKey.PANT -> "PNG" to "Pant"
                else -> "GLB" to item.typeClothes.capitalizeFirst()
            }

            tvExtension.text = extension
            tvTypeClothes.text = type

            when (item.typeClothes) {
                ValueKey.SHIRT -> loadImage(LoadClothesHelper.fullDomainImage(context, item.thumbnail), imvThumb)
                ValueKey.PANT -> loadImage(LoadClothesHelper.fullDomainImage(context, item.thumbnail), imvThumb)
                else -> loadImage(loadAccessory2DURL(item.thumbnail), imvThumb)
            }

            btnDownload.tap { onDownloadClick.invoke(item) }
        }
    }
}