package com.conchoback.haingon.ui.download

import android.content.Context
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.helper.LoadClothesHelper
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.key.ValueKey
import javax.inject.Inject

class DownloadRepository @Inject constructor() {

    suspend fun downloadClothesFileToExternal(context: Context, path: String, isAccessory: Boolean = false): Boolean {
        return MediaHelper.downloadAllToExternal(
            context = context,
            paths = listOf(
                if (!isAccessory) LoadClothesHelper.fullDomainImage(context, path)
                else loadAccessory2DURL(path)
            ),
            folderName = ValueKey.DOWNLOAD_ALBUM
        )
    }
}