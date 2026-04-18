package com.conchoback.haingon.ui.download

import android.content.Context
import android.widget.ImageView
import com.conchoback.haingon.core.base.BaseAdapter
import com.conchoback.haingon.core.extension.capitalizeFirst
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.loadImage
import com.conchoback.haingon.core.extension.tap
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
                ValueKey.SHIRT -> loadClothesImage(fullDomainImage(item.thumbnail), imvThumb)
                ValueKey.PANT -> loadClothesImage(fullDomainImage(item.thumbnail), imvThumb)
                else -> loadClothesImage(fullAccessoryPath(item.thumbnail), imvThumb)
            }

            btnDownload.tap { onDownloadClick.invoke(item) }
        }
    }

    fun loadClothesImage(image: String, imv: ImageView) {
        loadImage(image, imv, true)
    }

    fun fullDomainImage(image: String): String {
        return when {
            image.contains(ValueKey.CLOTHES_ALBUM) -> {
                // Internal
                val file = File(context.filesDir, image)
                file.absolutePath
            }

            image.contains(AssetsKey.COMBO_ASSET) || image.contains(AssetsKey.BASIC_ASSET) -> {
                // Asset
                "${AssetsKey.ASSET_MANAGER}/$image"
            }

            else -> {
                // api
                val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
                "${domain}${DomainKey.SUB_DOMAIN}/$image"
            }
        }
    }

    fun fullAccessoryPath(image: String): String {
        val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
        return "$domain${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_2D}/${image}.png"
    }

}