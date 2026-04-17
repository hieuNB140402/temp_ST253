package com.conchoback.haingon.ui.preview

import android.R.attr.type
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.helper.AssetHelper
import com.conchoback.haingon.core.helper.BitmapHelper
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.key.AssetsKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.io.File

class PreviewViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _clothesType = MutableStateFlow<String>("")
    val clothesType = _clothesType.asStateFlow()

    private val _clothesPath = MutableStateFlow<String>("")
    val clothesPath = _clothesPath.asStateFlow()
    // Normal Declaration
    //==================================================================================================================


    // Getter Setter
    //==================================================================================================================
    fun setData(clothesType: String, path: String) {
        _clothesType.value = clothesType
        _clothesPath.value = path
    }


    // Function feature
    //==================================================================================================================
    suspend fun sendImageFromPath(context: Context): String {
        val bitmap = if (_clothesPath.value.contains(AssetsKey.ASSET_MANAGER)) {
            AssetHelper.getBitmapFromAsset(context, _clothesPath.value)
        } else {
            try {
                BitmapFactory.decodeFile(_clothesPath.value)
            } catch (e: Exception) {
                eLog("sendImageFromPath: $e")
                return ""
            }
        }

        val outputStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

        val jsMethod =
            if (_clothesType.value == AssetsKey.SHIRT || _clothesType.value == AssetsKey.T_SHIRT) "setShirtFromBase64" else "setPantsFromBase64"

        return "window.$jsMethod('$base64')"
    }
}