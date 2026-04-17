package com.conchoback.haingon.ui.list

import android.R.attr.path
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.AccessorySelectedModel
import com.conchoback.haingon.data.model.PathAPI
import com.conchoback.haingon.data.model.SelectedModel
import com.conchoback.haingon.data.model.SubAccessoryModel
import com.google.common.collect.Multimaps.index
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.first

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

    var pathAccessorySelectedList = listOf<String>()


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
        Gson().fromJson<>()
        pathAccessorySelectedList = path.split(",")
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

    fun loadAccessoryList(): List<AccessorySelectedModel> {


        val models = _allData.value?.models ?: return emptyList()

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

        return config.map { (type, data, isSelected) ->
            val subList = loadSubAccessoryList(type, data)

            AccessorySelectedModel(
                typeAccessory = type,
                thumbnail = subList.first().accessory.path,
                subAccessoryList = subList,
                isSelected = isSelected
            )
        }
    }

    fun loadSubAccessoryList(subAccessory: String, subAccessoryList: List<String>): ArrayList<SubAccessoryModel> {
        val selectedSet = pathAccessorySelectedList.toSet()

        val list = subAccessoryList.map { item ->
            val path = "$subAccessory/$item"
            SubAccessoryModel(
                AccessoryModel(
                    typeAccessory = subAccessory,
                    path = path,
                ),
                isSelected = path in selectedSet
            )
        }.toCollection(ArrayList())

        val hasSelected = list.any { it.isSelected }

        list.add(
            0,
            SubAccessoryModel(
                AccessoryModel(
                    typeAccessory = subAccessory,
                    path = ValueKey.NONE_ACCESSORY,
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
            ""
        }
    }
}