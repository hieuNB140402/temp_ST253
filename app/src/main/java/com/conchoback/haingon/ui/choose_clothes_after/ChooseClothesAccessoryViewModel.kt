package com.conchoback.haingon.ui.choose_clothes_after

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.clothes.AccessoryModel
import com.conchoback.haingon.data.model.clothes.AccessorySelectedModel
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.data.model.clothes.AccessoryListType
import com.conchoback.haingon.data.model.clothes.SubAccessoryModel
import com.conchoback.haingon.ui.choose_clothes_after.adapter.CategoryAccessoryAdapter
import com.google.common.collect.Multimaps.index
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChooseClothesAccessoryViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _allData = MutableStateFlow<PathAPI?>(null)
    val allData: StateFlow<PathAPI?> = _allData.asStateFlow()

    private val _typeClothes = MutableStateFlow<String>("")
    val typeClothes: StateFlow<String> = _typeClothes.asStateFlow()

    private val _clothesList = MutableStateFlow<List<SelectedModel>>(emptyList())
    val clothesList: StateFlow<List<SelectedModel>> = _clothesList.asStateFlow()


    // Normal Declaration
    //==================================================================================================================

    var pathClothesSelected = ""

    var accessoryList = emptyList<AccessorySelectedModel>()
    val accessorySelectedList = ArrayList<AccessoryModel>()


    // Getter Setter
    //==================================================================================================================
    fun setAllData(data: PathAPI) {
        _allData.value = data
    }

    fun setTypeClothes(type: String) {
        _typeClothes.value = type
    }

    fun setClothesList(list: List<SelectedModel>) {
        _clothesList.value = list
    }


    fun updatePathClothesSelected(path: String) {
        pathClothesSelected = path
    }

    fun updatePathAccessorySelectedList(path: String) {
        if (path != "") {
            val type = AccessoryListType().type
            val list: List<AccessoryModel> = Gson().fromJson(path, type)
            accessorySelectedList.addAll(list)
        }
    }

    fun updateAccessoryList(list: List<AccessorySelectedModel>) {
        accessoryList = list
    }

    // Function feature
    //==================================================================================================================

    /* Clothes */
    suspend fun loadClothesList(pathClothesSelected: String): List<SelectedModel> {
        updatePathClothesSelected(pathClothesSelected)

        // folder clothes
        val folder = _allData.value?.folders?.firstOrNull() ?: return emptyList()

        val clothesList = ArrayList<SelectedModel>()

        for (index in 1..folder.quantity) {

            // VD: category/1.png (API)
            val path = "${folder.category}/$index.png"

            clothesList.add(
                SelectedModel(
                    path = path,
                    isSelected = path == pathClothesSelected
                )
            )
        }
        setClothesList(clothesList)
        return clothesList
    }

    fun selectClothes(path: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _clothesList.value = _clothesList.value.mapIndexed { index, clothesModel ->
                clothesModel.copy(isSelected = index == position)
            }

            updatePathClothesSelected(path)
        }
    }

    suspend fun getPositionClothesSelected(list: List<SelectedModel>): Int {
        return list.indexOfFirst { it.isSelected }
    }

    /* Accessory */
    suspend fun loadAccessoryList(pathAccessorySelectedList: String) {
        updatePathAccessorySelectedList(pathAccessorySelectedList)

        val models = _allData.value?.models ?: return updateAccessoryList(emptyList())

        val config = listOf(
            Triple(ValueKey.GLASSES_SUB_ACCESSORY, models.glasses, true),
            Triple(ValueKey.HAIR_SUB_ACCESSORY, models.hair, false),
            Triple(ValueKey.HAT_SUB_ACCESSORY, models.hat, false),
            Triple(ValueKey.LEFTHAND_SUB_ACCESSORY, models.lefthand, false),
            Triple(ValueKey.NECK_SUB_ACCESSORY, models.neck, false),
            Triple(ValueKey.RIGHTHAND_SUB_ACCESSORY, models.righthand, false),
            Triple(ValueKey.SHOULDER_SUB_ACCESSORY, models.shoulder, false),
            Triple(ValueKey.WAIST_SUB_ACCESSORY, models.waist, false),
            Triple(ValueKey.WING_SUB_ACCESSORY, models.wing, false)
        )

        val accessoryList = config.map { (type, data, isSelected) ->
            val subList = loadSubAccessoryList(type, data)

            AccessorySelectedModel(
                typeAccessory = type,
                // [0] = none
                thumbnail = subList[1].accessory.value,
                subAccessoryList = subList,
                isSelected = isSelected
            )
        }

        updateAccessoryList(accessoryList)
    }

    fun loadSubAccessoryList(subAccessory: String, subAccessoryList: List<String>): ArrayList<SubAccessoryModel> {
        val selectedSet = accessorySelectedList.map { it.value }.toSet()

        val list = subAccessoryList.map { item ->
            val path = "$subAccessory/$item"
            SubAccessoryModel(
                accessory = AccessoryModel(
                    key = subAccessory,
                    value = path,
                ),
                isSelected = path in selectedSet
            )
        }.toCollection(ArrayList())

        val hasSelected = list.any { it.isSelected }

        list.add(
            0,
            SubAccessoryModel(
                accessory = AccessoryModel(
                    key = subAccessory,
                    value = ValueKey.NONE_ACCESSORY,
                ),
                isSelected = !hasSelected
            )
        )

        return list
    }

    fun getPathClotheAccessorySelected(): String {
        return if (_typeClothes.value != ValueKey.ACCESSORY) {
            pathClothesSelected
        } else {
            // accessory
            Gson().toJson(accessorySelectedList)
        }
    }

    suspend fun refocusSubAccessory(model: AccessoryModel, position: Int): List<SubAccessoryModel> {
        var positionCategoryAccessory = 0

        accessoryList = accessoryList.mapIndexed { index, accessorySelectedModel ->
            if (accessorySelectedModel.typeAccessory != model.key) {
                accessorySelectedModel
            } else {
                positionCategoryAccessory = index

                accessorySelectedModel.copy(
                    subAccessoryList = accessorySelectedModel.subAccessoryList.mapIndexed { indexSub, subModel ->
                        subModel.copy(isSelected = indexSub == position)
                    }
                )
            }
        }

        // update accessorySelectedList (return)
        if (position != 0) {
            if (accessorySelectedList.any { it.key == model.key }) {
                accessorySelectedList.removeIf { it.key == model.key }
                accessorySelectedList.add(model)
            } else {
                accessorySelectedList.add(model)
            }
        } else {
            accessorySelectedList.removeIf { it.key == model.key }
        }

        return accessoryList[positionCategoryAccessory].subAccessoryList
    }

    suspend fun refocusAccessory(position: Int) {
        accessoryList = accessoryList.mapIndexed { index, accessorySelectedModel ->
            accessorySelectedModel.copy(isSelected = index == position)
        }
    }

    suspend fun getPositionSubAccessorySelected(subAccessoryList: List<SubAccessoryModel>): Int {
        return subAccessoryList.indexOfFirst { it.isSelected }
    }
}