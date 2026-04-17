package com.conchoback.haingon.ui.view3d

import android.content.Context
import android.webkit.WebResourceResponse
import androidx.webkit.WebViewAssetLoader
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection

class InternalStoragePathHandler(
    private val context: Context
) : WebViewAssetLoader.PathHandler {

    override fun handle(path: String): WebResourceResponse? {
        return try {
            val file = File(context.filesDir, path)

            if (!file.exists()) return null

            val inputStream = FileInputStream(file)

            WebResourceResponse(
                getMimeType(file.name),
                "UTF-8",
                inputStream
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun getMimeType(name: String): String {
        return URLConnection.guessContentTypeFromName(name) ?: "application/octet-stream"
    }
}