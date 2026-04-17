package com.conchoback.haingon.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.conchoback.haingon.core.extension.dLog
import com.conchoback.haingon.core.extension.eLog
import com.conchoback.haingon.core.helper.InternetHelper
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.helper.SharePreferenceHelper
import com.conchoback.haingon.core.service.RetrofitClient
import com.conchoback.haingon.core.service.RetrofitPreventive
import com.conchoback.haingon.core.utils.DataLocal
import com.conchoback.haingon.core.utils.key.ValueKey
import com.conchoback.haingon.core.utils.state.CallApiState
import com.conchoback.haingon.data.model.PathAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

class DataViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _allData = MutableStateFlow<PathAPI?>(null)
    val allData: StateFlow<PathAPI?> = _allData.asStateFlow()

    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================

    // Function feature
    //==================================================================================================================
    suspend fun getAllParts(context: Context, sharePreferenceHelper: SharePreferenceHelper): Flow<CallApiState<PathAPI>> = flow {
        emit(CallApiState.Loading)

        val response = withTimeoutOrNull(5_000) {
            try {
                RetrofitClient.api.getAllData()
            } catch (e: Exception) {
                DataLocal.isFailBaseURL = true
                Log.e("nbhieu", "BASE_URL failed: ${e.message}")
                null
            }
        } ?: withTimeoutOrNull(5_000) {
            try {
                RetrofitPreventive.api.getAllData()
            } catch (e: Exception) {
                DataLocal.isFailBaseURL = false
                Log.e("nbhieu", "BASE_URL_PREVENTIVE failed: ${e.message}")
                null
            }
        }

        if (response != null && response.isSuccessful && response.body() != null) {
            dLog("response: ${response.body()}")
            withContext(Dispatchers.IO) {
                val data = response.body()!!
                val folderClothes = data.folders.first()
                if (folderClothes.quantity > 1){
                    sharePreferenceHelper.setFirstClothes("${folderClothes.category}/1.png")
                }
                MediaHelper.writeModelToFile(context, ValueKey.DATA_FILE_API_INTERNAL, data)
            }
            emit(CallApiState.Success(response.body()!!))
        } else {
            DataLocal.isFailBaseURL = true
            emit(CallApiState.Error("null"))
        }
    }

    suspend fun checkCurrentVersion(context: Context) {
        val fileAPI = File(context.filesDir, ValueKey.DATA_FILE_API_INTERNAL)
        if (fileAPI.exists()) fileAPI.delete()

//        val currentVersion = BuildConfig.VERSION_NAME
//        if (currentVersion != sharePreference.getCurrentVersion()) {
//            sharePreference.setCurrentVersion(currentVersion)
//
//            val file = File(filesDir, ValueKey.DATA_FILE_INTERNAL)
//            val fileAvatar = File(filesDir, ValueKey.EDIT_FILE_INTERNAL)
//
//            if (fileAvatar.exists()) fileAvatar.delete()
//
//            if (file.exists()) file.delete()
//        }
//        ensureData(context, sharePreferenceHelper)
    }

    fun ensureData(context: Context, sharePreferenceHelper: SharePreferenceHelper) {
        if (_allData.value == null) {
            saveAndReadData(context, sharePreferenceHelper)
        }
    }

    fun saveAndReadData(context: Context, sharePreferenceHelper: SharePreferenceHelper) {
        viewModelScope.launch {
            val timeStart = System.currentTimeMillis()
            val model = withContext(Dispatchers.IO) {

                var dataApi = MediaHelper.readModelFromFile<PathAPI>(context, ValueKey.DATA_FILE_API_INTERNAL) ?: null

                if (dataApi == null && InternetHelper.checkInternet(context)) {
                    getAllParts(context, sharePreferenceHelper).collect { state ->
                        when (state) {
                            CallApiState.Loading -> {}
                            is CallApiState.Success -> dataApi = state.model
                            is CallApiState.Error -> eLog("saveAndReadData: ${state.e}")
                        }
                    }
                }

                dataApi
            }

            _allData.value = model

            val timeEnd = System.currentTimeMillis()
            dLog("Time saveAndReadData: ${timeEnd - timeStart}")
        }
    }
}