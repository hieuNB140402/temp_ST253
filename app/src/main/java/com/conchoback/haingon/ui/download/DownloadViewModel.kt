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
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

@HiltViewModel
class DownloadViewModel @Inject constructor(private val downloadRepository: DownloadRepository) : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _jsonList = MutableStateFlow<String>("")
    val jsonList: StateFlow<String> = _jsonList.asStateFlow()


    // Normal Declaration
    //==================================================================================================================
    var downloadModel = DownloadModel("", "")

    // Getter Setter
    //==================================================================================================================
    fun setJsonList(json: String) {
        if (json == "") return
        _jsonList.value = json
    }

    fun updateDownloadModel(model: DownloadModel){
        downloadModel = model
    }

    // Function feature
    //==================================================================================================================
    suspend fun convertFromJson(): List<DownloadModel> {
        val type = object : TypeToken<List<DownloadModel>>() {}.type
        val list: List<DownloadModel> = Gson().fromJson(_jsonList.value, type)

        return list
    }

    suspend fun handleDownload(context: Context): Boolean {
        return downloadRepository.downloadClothesFileToExternal(
            context,
            downloadModel.thumbnail,
            downloadModel.typeClothes != ValueKey.SHIRT && downloadModel.typeClothes != ValueKey.PANT
        )
    }

}