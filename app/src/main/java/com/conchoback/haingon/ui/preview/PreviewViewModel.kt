package com.conchoback.haingon.ui.preview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import com.conchoback.haingon.core.extension.capitalizeFirst
import com.conchoback.haingon.core.extension.loadAccessory2DURL
import com.conchoback.haingon.core.helper.LoadClothesHelper
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.data.model.MyCreationModel
import com.conchoback.haingon.ui.download.DownloadRepository
import com.conchoback.haingon.ui.home.DataRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: DataRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _clothesJson = MutableStateFlow<String>("")
    val clothesJson = _clothesJson.asStateFlow()

    // Normal Declaration
    //==================================================================================================================
    var id = -1
    var currentClothes = DownloadModel("", "")

    // Getter Setter
    //==================================================================================================================
    fun setClothesJson(value: String) {
        _clothesJson.value = value
    }

    fun updateId(value: Int) {
        id = value
    }

    fun updateCurrentClothes(model: DownloadModel) {
        currentClothes = model
    }

    // Function feature
    //==================================================================================================================
    fun convertJson(context: Context): Triple<String, String, String> {
        val model = Gson().fromJson(_clothesJson.value, MyCreationModel::class.java)

        val clothes = model.clothes
        updateId(model.id)
        updateCurrentClothes(clothes)

        val (extension, type) = when (clothes.typeClothes) {
            ValueKey.SHIRT -> "PNG" to "Shirt"
            ValueKey.PANT -> "PNG" to "Pant"
            else -> "GLB" to clothes.typeClothes.capitalizeFirst()
        }

        val fullPathThumb = when (clothes.typeClothes) {
            ValueKey.SHIRT -> LoadClothesHelper.fullDomainImage(context, clothes.thumbnail)
            ValueKey.PANT -> LoadClothesHelper.fullDomainImage(context, clothes.thumbnail)
            else -> loadAccessory2DURL(clothes.thumbnail)
        }

        return Triple(fullPathThumb, type, extension)
    }

    // Room
    suspend fun deleteClothesSavedById() {
        repository.deleteClothesSavedById(id)
    }

    suspend fun downloadClothesFileToExternal(context: Context): Boolean {
        return downloadRepository.downloadClothesFileToExternal(context, currentClothes.thumbnail,
            currentClothes.typeClothes != ValueKey.SHIRT && currentClothes.typeClothes != ValueKey.PANT)
    }

}