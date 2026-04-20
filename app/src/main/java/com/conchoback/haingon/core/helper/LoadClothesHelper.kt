package com.conchoback.haingon.core.helper

import android.content.Context
import com.conchoback.haingon.core.extension.domain
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.ValueKey
import java.io.File

object LoadClothesHelper {

    fun fullDomainImage(context: Context, image: String): String {
        return when {
            image.contains(ValueKey.CLOTHES_ALBUM) -> {
                // Internal
                val file = File(context.filesDir, image)
                file.absolutePath
            }

            image.contains(AssetsKey.COMBO_ASSET) || image.contains(AssetsKey.BASIC_ASSET) || image.contains(AssetsKey.TRENDING_ASSET)-> {
                // Asset
                "${AssetsKey.ASSET_MANAGER}/$image"
            }

            else -> {
                // api
                domain("${DomainKey.BASE_PATH}/$image")
            }
        }
    }
}