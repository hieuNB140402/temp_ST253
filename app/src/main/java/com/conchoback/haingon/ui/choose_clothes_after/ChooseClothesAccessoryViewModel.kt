package com.conchoback.haingon.ui.choose_clothes_after

import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.data.model.SubAccessoryModel
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChooseClothesAccessoryViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _allData = MutableStateFlow<PathAPI?>(null)
    val allData: StateFlow<PathAPI?> = _allData.asStateFlow()

    private val _typeClothes = MutableStateFlow<String>("")
    val typeClothes: StateFlow<String> = _typeClothes.asStateFlow()


    // Normal Declaration
    //==================================================================================================================

    var pathClothesSelected = ""

    val accessoryList = ArrayList<AccessorySelectedModel>()
    val accessorySelectedList = ArrayList<AccessoryModel>()


    // Getter Setter
    //==================================================================================================================
    fun setAllData(data: PathAPI) {
        _allData.value = data
    }

    fun setTypeClothes(type: String) {
        _typeClothes.value = type
    }

    fun updatePathClothesSelected(path: String) {
        pathClothesSelected = path
    }

    fun updatePathAccessorySelectedList(path: String) {
        if (path != "") {
            val type = object : TypeToken<List<AccessoryModel>>() {}.type
            val list: List<AccessoryModel> = Gson().fromJson(path, type)
            accessorySelectedList.addAll(list)
        }
    }

    fun updateAccessoryList(list: List<AccessorySelectedModel>) {
        accessoryList.clear()
        accessoryList.addAll(list)
    }

    suspend fun updateCategoryAccessoryList(position: Int) {
        accessoryList.forEachIndexed { index, model ->
            model.isSelected = index == position
        }
    }

    // Function feature
    //==================================================================================================================
    suspend fun loadClothesList(pathClothesSelected: String): List<SelectedModel> {

        updatePathClothesSelected(pathClothesSelected)

        val folder = _allData.value?.folders?.firstOrNull() ?: return emptyList()

        val returnList = ArrayList<SelectedModel>()
        for (index in 1..folder.quantity) {
            val path = "${folder.category}/$index.png"
            returnList.add(
                SelectedModel(
                    path = path,
                    isSelected = path == pathClothesSelected
                )
            )
        }
        return returnList
    }

    suspend fun loadAccessoryList(pathAccessorySelectedList: String) {
        updatePathAccessorySelectedList(pathAccessorySelectedList)

        val models = _allData.value?.models ?: return updateAccessoryList(emptyList())

        val config = listOf(
            Triple(ValueKey.GLASSES_SUB_ACCESSORY, models.glasses, true),
            Triple(ValueKey.HAIR_SUB_ACCESSORY, models.hair, false),
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
                AccessoryModel(
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
                AccessoryModel(
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
            val json = Gson().toJson(accessorySelectedList)
            json
        }
    }

    suspend fun changeSubAccessory(model: AccessoryModel, position: Int): Int {
        var positionCategoryAccessory = 0
        accessoryList.forEachIndexed { index, accessorySelectedModel ->
            if (accessorySelectedModel.typeAccessory == model.key) {
                positionCategoryAccessory = index
                accessorySelectedModel.subAccessoryList.forEachIndexed { index, subAccessoryModel ->
                    subAccessoryModel.isSelected = index == position
                }
            }
        }

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

        return positionCategoryAccessory
    }

}