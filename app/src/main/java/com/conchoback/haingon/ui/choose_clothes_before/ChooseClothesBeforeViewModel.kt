package com.conchoback.haingon.ui.choose_clothes_before

import android.content.Context
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.App
import com.conchoback.haingon.core.helper.AssetHelper
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.AccessorySelectedModel
import com.conchoback.haingon.data.model.SelectedModel
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
    var typeCombo = ValueKey.BASIC_SKIN

    // Getter Setter
    //==================================================================================================================
    fun setTypeClothes(type: String) {
        _typeClothes.value = type
    }

    fun updateTypeCombo(type: String) {
        typeCombo = type
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
                            AssetsKey.SHIRT_BASIC
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
                            AssetsKey.PANT_BASIC
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
            parentList.subAccessoryList.forEachIndexed { index, child ->
                if (index != 0) {
                    returnList.add(child.accessory)
                }
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

    suspend fun loadComboList(context: Context, list: List<SelectedModel>): List<String> {
        return if (isSpecialCombo()) {
            mergeComboList(list)
        } else {
            if (App.instant.comboListDefault.isEmpty()) {
                App.instant.reupdateComboListDefault(AssetHelper.getSubfoldersAssetWithSubDomain(context, AssetsKey.SHIRT_COMBO))
            }

            App.instant.comboListDefault
        }
    }

    fun isSpecialCombo(): Boolean {
        return typeCombo == ValueKey.SPECIAL_SKIN
    }

    suspend fun mergeComboList(list: List<SelectedModel>): List<String> {
        return list.map { it.path }
    }
}