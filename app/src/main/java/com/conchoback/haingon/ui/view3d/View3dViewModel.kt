package com.conchoback.haingon.ui.view3d

import android.app.Activity
import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.webkit.WebViewAssetLoader
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.extension.iLog
import com.conchoback.haingon.core.extension.loadAccessory3DURL
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.DomainKey
import com.conchoback.haingon.core.utils.key.IntentKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.local.ClothesSaved
import com.conchoback.haingon.data.model.DownloadListType
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.ClothesModel
import com.conchoback.haingon.data.model.clothes.AccessoryListType
import com.conchoback.haingon.ui.home.DataRepository
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.net.HttpURLConnection
import java.net.URL

@HiltViewModel
class View3dViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _state = MutableStateFlow(View3dState())
    val state: StateFlow<View3dState> = _state.asStateFlow()

    val typeClothes = state.select(viewModelScope) { it.typeClothes }
    val themeFlow = state.select(viewModelScope) { it.theme }
    val typeCharacterFlow = state.select(viewModelScope) { it.typeCharacter }
    val shirtFlow = state.select(viewModelScope) { it.shirt }
    val pantFlow = state.select(viewModelScope) { it.pant }
    val accessoryFlow = state.select(viewModelScope) { it.accessories }
    val isShowFeatureFlow = state.select(viewModelScope) { it.isShowFeature }

    // Normal Declaration
    //==================================================================================================================
    var withLayoutFeature = 0
    var typeClothesSelected = ""

    // Getter Setter
    //==================================================================================================================
    fun dispatch(action: View3dAction) {
        val current = _state.value

        _state.value = when (action) {
            is View3dAction.ChangeTypeClothes -> current.copy(typeClothes = action.typeClothes)

            is View3dAction.ChangeTheme -> current.copy(theme = action.theme)

            is View3dAction.ChangeTypeCharacter -> current.copy(typeCharacter = action.typeCharacter)

            is View3dAction.ChangeShirt -> current.copy(shirt = action.itemShirt)

            is View3dAction.ChangePant -> current.copy(pant = action.itemPant)

            is View3dAction.ChangeAccessory -> current.copy(accessories = action.accessoryList)

            View3dAction.ClearAccessory -> current.copy(accessories = emptyList())

            is View3dAction.ChangeShowFeature -> {
                typeClothesSelected = action.typeClothesSelected
                current.copy(isShowFeature = action.isShowFeature)
            }
        }
    }


    fun updateWithLayoutFeature(w: Int) {
        withLayoutFeature = w
    }

    // Function feature
    //==================================================================================================================
    fun <T, R> StateFlow<T>.select(scope: CoroutineScope, selector: (T) -> R): StateFlow<R> {
        return this
            .map(selector)
            .distinctUntilChanged()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = selector(this.value)
            )
    }

    fun loadWebView(context: Context, request: WebResourceRequest): WebResourceResponse? {

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .addPathHandler("/internal/", InternalStoragePathHandler(context))
            .build()

        val url = request.url.toString()

        if (url.contains(DomainKey.DOMAIN) || url.contains(DomainKey.DOMAIN_PREVENTIVE)) {
            return try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val mimeType = connection.contentType ?: "image/png"

                val responseHeaders = mutableMapOf<String, String>().apply {
                    put("Access-Control-Allow-Origin", "*")
                    put("Access-Control-Allow-Methods", "GET, OPTIONS")
                }

                WebResourceResponse(
                    mimeType,
                    connection.contentEncoding ?: "UTF-8",
                    200,
                    "OK",
                    responseHeaders,
                    connection.inputStream
                )
            } catch (e: Exception) {
                eLog("loadWebView: $e")
                null
            }
        }

        return assetLoader.shouldInterceptRequest(request.url)
    }

    fun getIndexCharacter(character: String): Int {
        return when (character) {
            ValueKey.CHARACTER_1 -> 0
            ValueKey.CHARACTER_2 -> 1
            ValueKey.CHARACTER_3 -> 2
            else -> 3
        }
    }

    fun isAccessory(): Boolean {
        return typeClothes.value == ValueKey.ACCESSORY
    }

    fun updateClothesEdit(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val editedClothes = data?.getStringExtra(IntentKey.EDITED_CLOTHES_KEY) ?: ""

            if (editedClothes != "") {
                // Quần hoặc áo (edit)
                val clothes = data?.getStringExtra(IntentKey.TYPE_CLOTHES_KEY)

                if (clothes == ValueKey.SHIRT) {
                    dispatch(View3dAction.ChangeShirt(ClothesModel(ValueKey.SHIRT, editedClothes)))
                } else {
                    dispatch(View3dAction.ChangePant(ClothesModel(ValueKey.PANT, editedClothes)))
                }

            } else {
                // Tất cả (Chọn từ api)
                val clothes = data?.getStringExtra(IntentKey.TYPE_CLOTHES_KEY)
                val clothesSelected = data?.getStringExtra(IntentKey.CHOOSE_CLOTHES_KEY) ?: ""

                when (clothes) {
                    ValueKey.SHIRT -> {
                        dispatch(View3dAction.ChangeShirt(ClothesModel(ValueKey.SHIRT, clothesSelected)))
                    }

                    ValueKey.PANT -> {
                        dispatch(View3dAction.ChangePant(ClothesModel(ValueKey.PANT, clothesSelected)))
                    }

                    ValueKey.ACCESSORY -> {
                        dispatch(View3dAction.ChangeAccessory(convertFromJsonAccessory(clothesSelected)))
                    }
                }
            }


        }
    }

    fun convertFromJsonAccessory(clothesSelected: String): List<AccessoryModel> {
//        val type = object : TypeToken<List<AccessoryModel>>() {}.type
        val type = AccessoryListType().type
        val list: List<AccessoryModel> = Gson().fromJson(clothesSelected, type)
        return list
    }

    fun loadClothes(imagePath: String): String {
        return when {
            // cache (edit)
            imagePath.contains(ValueKey.TEMP_ALBUM) -> "${AssetsKey.DOMAIN_INTERNAL_WEBVIEW}/$imagePath"
            // api
            imagePath.contains(DomainKey.SPECIAL_CATEGORY) -> {
                val domain = if (DataLocal.isFailBaseURL) DomainKey.DOMAIN_PREVENTIVE else DomainKey.DOMAIN
                "$domain${DomainKey.BASE_PATH}/${imagePath}"
            }
            // asset
            else -> "${AssetsKey.DOMAIN_ASSET_WEBVIEW}/$imagePath"
        }
    }

    fun getClothesByType(type: String): String {
        return when (type) {
            ValueKey.SHIRT -> shirtFlow.value!!.item
            ValueKey.PANT -> pantFlow.value!!.item
            else -> {
                if (accessoryFlow.value.isNotEmpty()) {
                    val json = Gson().toJson(accessoryFlow.value)
                    json
                } else {
                    ""
                }
            }
        }
    }

    fun loadComboPath(path: String): Pair<String, String> {
        return if (path.contains(AssetsKey.COMBO_ASSET)) {
            val fileName = path.split("/").last()
            val shirtPath = "${AssetsKey.SHIRT_COMBO}/$fileName"
            val pantPath = "${AssetsKey.SHIRT_COMBO}/$fileName"

            shirtPath to pantPath
        } else {
            path to path
        }
    }

    suspend fun getDownloadData(context: Context): List<DownloadModel> {
        val list = ArrayList<DownloadModel>()

        val shirtModel = shirtFlow.value
        val pantModel = pantFlow.value
        val accessoryModel = accessoryFlow.value

        // Cache Album -> Clothes Album
        suspend fun moveIfInternalFile(currentPath: String): String {
            return if (currentPath.contains(ValueKey.TEMP_ALBUM)) {
                val fileName = currentPath.split("/").last()

                MediaHelper.moveInternalFile(context, currentPath, "${ValueKey.CLOTHES_ALBUM}/$fileName")
            } else {
                currentPath
            }
        }

        suspend fun addShirt() {
            if (shirtModel == null) return

            val thumb = moveIfInternalFile(shirtModel.item)
            list.add(
                DownloadModel(
                    typeClothes = shirtModel.typeClothes,
                    thumbnail = thumb,
                )
            )
        }

        suspend fun addPant() {
            if (pantModel == null) return

            val thumb = moveIfInternalFile(pantModel.item)
            list.add(
                DownloadModel(
                    typeClothes = pantModel.typeClothes,
                    thumbnail = thumb,
                )
            )
        }

        when (typeClothes.value) {
            ValueKey.SHIRT -> addShirt()

            ValueKey.PANT -> addPant()

            ValueKey.COMBO -> {
                addShirt()
                addPant()
            }

            ValueKey.ACCESSORY -> {
                addShirt()
                addPant()

                if (accessoryModel.isNotEmpty()) {
                    accessoryModel.forEach { item ->
                        list.add(
                            DownloadModel(
                                typeClothes = item.key,
                                thumbnail = item.value,
                            )
                        )
                    }
                }
            }
        }

        repository.insertClothesSavedList(
            list.map {
                ClothesSaved(
                    typeClothes = it.typeClothes,
                    thumbnail = it.thumbnail
                )
            }
        )

        return list
    }

    // Webview Function
    //==================================================================================================================
    fun updateTheme(theme: String): String {
        return "window.updateTheme('$theme')"
    }

    fun updateItem(vararg items: Pair<String, String>, charId: String? = null): String {

        val jsonArray = items.joinToString(prefix = "[", postfix = "]") { (key, value) ->
            "{key: '$key', value: '${loadClothes(value)}'}"
        }

        val charIdParam = charId?.let { ", '$it'" } ?: ""

        return "window.setItems($jsonArray$charIdParam)"
    }

    fun updateCharacter(character: String): String {
        return "window.switchCharacter('$character')"
    }

    fun updateAccessory(accessoryList: List<AccessoryModel>): String {
        val jsonList = accessoryList.map {
            AccessoryModel(
                key = it.key,
                value = loadAccessory3DURL(it.value)
            )
        }

        // [key ="", value =""]
        val json = Gson().toJson(jsonList)

        return "window.setAccessories('$json')"
    }
}