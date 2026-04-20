package com.conchoback.haingon.ui.home.view_model

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.core.utils.state.DeleteState
import com.conchoback.haingon.data.local.ClothesSaved
import com.conchoback.haingon.data.model.DownloadModel
import com.conchoback.haingon.data.model.MyCreationModel
import com.conchoback.haingon.ui.home.DataRepository
import com.google.common.collect.Multimaps.index
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: DataRepository) : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    /* Home Activity */
    private val _currentTab = MutableStateFlow(-1)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()


    /* My Creation */
    private val _myCreationList = MutableStateFlow<List<MyCreationModel>>(arrayListOf())
    val myCreationList: StateFlow<List<MyCreationModel>> = _myCreationList.asStateFlow()

    private val _isShowSelection = MutableStateFlow(false)
    val isShowSelection: StateFlow<Boolean> = _isShowSelection.asStateFlow()


    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================
    /* Home Activity */
    fun setCurrentTab(index: Int) {
        _currentTab.value = index
    }

    /* My Creation */
    suspend fun setMyCreationList(list: List<ClothesSaved>) {
        _myCreationList.value = list.map {
            MyCreationModel(
                id = it.id,
                clothes = DownloadModel(
                    typeClothes = it.typeClothes,
                    thumbnail = it.thumbnail
                ),
                isSelected = false,
                isShowSelection = false
            )
        }.reversed()
    }

    fun touchSelectMyCreation(indexTouch: Int) {
        _myCreationList.value = _myCreationList.value.mapIndexed { index, model ->
            model.copy(isSelected = if (index == indexTouch) !model.isSelected else model.isSelected)
        }
    }

    fun showSelectMyCreation(indexTouch: Int) {
        setSelectionState(true)
        _myCreationList.value = _myCreationList.value.mapIndexed { index, model ->
            model.copy(
                isSelected = if (index == indexTouch) !model.isSelected else model.isSelected,
                isShowSelection = true
            )
        }
    }

    fun hideSelectMyCreation() {
        setSelectionState(false)
        _myCreationList.value = _myCreationList.value.map { model ->
            model.copy(
                isSelected = false,
                isShowSelection = false
            )
        }
    }

    fun setSelectionState(state: Boolean) {
        _isShowSelection.value = state
    }


    suspend fun deleteMyCreation(): DeleteState {
        val listSelected = getItemSelected()
        if (listSelected.isEmpty()) return DeleteState.Empty

        return try {
            repository.deleteClothesSavedByIds(listSelected.map { it.id })
            setMyCreationList(repository.getAllClothesSaved())
            DeleteState.Success

        } catch (e: Exception) {
            DeleteState.Failure(e.message)
        }

    }

    suspend fun getItemSelected(): List<MyCreationModel> {
        return _myCreationList.value.filter { it.isSelected }
    }

    fun reSubmitMyCreation(data: ActivityResult) {
        if (data.resultCode == Activity.RESULT_OK) {
            viewModelScope.launch(Dispatchers.IO) {
                setMyCreationList(repository.getAllClothesSaved())
            }
        }
    }


    // Function feature
    //==================================================================================================================
    suspend fun deleteCacheFolder(context: Context) {
        MediaHelper.clearFolder(context, ValueKey.TEMP_ALBUM)
    }

    fun getClothesPath(index: Int): String {
        val preDomain = AssetsKey.TRENDING_ASSET
        return "$preDomain/$index.png"
    }

    // Room
    //==================================================================================================================
    suspend fun getAllClothesSaved() {
        setMyCreationList(repository.getAllClothesSaved())
    }

    suspend fun deleteClothesSavedByIds(ids: List<Int>) {
        repository.deleteClothesSavedByIds(ids)
    }

    suspend fun insertClothesSavedList(clothesSavedList: List<ClothesSaved>) {
        repository.insertClothesSavedList(clothesSavedList)
    }
}