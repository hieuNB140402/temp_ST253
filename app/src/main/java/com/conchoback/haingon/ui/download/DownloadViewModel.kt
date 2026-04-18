package com.conchoback.haingon.ui.download

import android.R.attr.path
import android.content.Context
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class DownloadViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _jsonList = MutableStateFlow<String>("")
    val jsonList: StateFlow<String> = _jsonList.asStateFlow()


    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================
    fun setJsonList(json: String) {
        if (json == "") return
        _jsonList.value = json
    }


    // Function feature
    //==================================================================================================================
    suspend fun convertFromJson(): List<DownloadModel> {
        val type = object : TypeToken<List<DownloadModel>>() {}.type
        val list: List<DownloadModel> = Gson().fromJson(_jsonList.value, type)

        return list
    }

    suspend fun handleDownload(context: Context, model: DownloadModel) : Boolean{
        return MediaHelper.downloadAllToExternal(
            context = context,
            paths = listOf(fullDomainImage(context, model)),
            folderName = ValueKey.DOWNLOAD_ALBUM
        )
    }

    fun fullDomainImage(context: Context, model: DownloadModel): String {
        return when {
            model.thumbnail.contains(ValueKey.CLOTHES_ALBUM) -> {
                // Internal
                val file = File(context.filesDir, model.thumbnail)
                file.absolutePath
            }

            model.thumbnail.contains(AssetsKey.COMBO_ASSET) || model.thumbnail.contains(AssetsKey.BASIC_ASSET) -> {
                // Asset
                "${AssetsKey.ASSET_MANAGER}/${model.thumbnail}"
            }

            else -> {
                // api
                val domain = if (DataLocal.isFailBaseURL) DomainKey.BASE_URL_PREVENTIVE else DomainKey.BASE_URL
                if (model.typeClothes != ValueKey.SHIRT && model.typeClothes != ValueKey.PANT){
                    "${domain}${DomainKey.SUB_DOMAIN}/${DomainKey.PREVIEW_3D}/${model.thumbnail}.glb"
                }else{
                    "${domain}${DomainKey.SUB_DOMAIN}/${model.thumbnail}"
                }
            }
        }
    }
}