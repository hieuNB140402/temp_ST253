package com.conchoback.haingon.ui.choose_clothes_before

import android.content.Context
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.App
import com.conchoback.haingon.core.helper.AssetHelper
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChooseClothesBeforeViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _typeClothes = MutableStateFlow<String>("")
    val typeClothes: StateFlow<String> = _typeClothes.asStateFlow()


    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================
    fun setTypeClothes(type: String) {
        _typeClothes.value = type
    }

    // Function feature
    //==================================================================================================================
    suspend fun loadClothesList(context: Context): List<String> {
        return when (_typeClothes.value) {
            ValueKey.SHIRT -> {
                if (App.instant.shirtListDefault.isEmpty()) {
                    App.instant.reupdateShirtListDefault(
                        AssetHelper.getSubfoldersAssetWithSubDomain(
                            context,
                            AssetsKey.SHIRT_DEFAULT
                        )
                    )
                }

                App.instant.shirtListDefault
            }

            ValueKey.PANT -> {
                if (App.instant.pantListDefault.isEmpty()) {
                    App.instant.reupdatePantListDefault(
                        AssetHelper.getSubfoldersAssetWithSubDomain(
                            context,
                            AssetsKey.PANT_DEFAULT
                        )
                    )
                }

                App.instant.pantListDefault
            }

            else -> emptyList()
        }
    }

    suspend fun mergeAccessoryList(list: List<AccessorySelectedModel>): List<AccessoryModel> {
        val returnList = ArrayList<AccessoryModel>()

        list.forEach { parentList ->
            parentList.subAccessoryList.forEach { childList ->
                returnList.add(childList.accessory)
            }
        }

        return returnList
    }

    fun convertToJson(model: AccessoryModel): String {
        val modelList = ArrayList<AccessoryModel>()
        modelList.add(model)
        val json = Gson().toJson(modelList)
        return json
    }

}