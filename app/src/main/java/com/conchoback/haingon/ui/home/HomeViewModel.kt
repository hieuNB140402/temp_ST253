package com.conchoback.haingon.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.conchoback.haingon.core.helper.MediaHelper
import com.conchoback.haingon.core.utils.key.AssetsKey
import com.conchoback.haingon.core.utils.key.ValueKey

class HomeViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================

    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================

    // Function feature
    //==================================================================================================================
    suspend fun deleteCacheFolder(context: Context){
        MediaHelper.clearFolder(context, ValueKey.TEMP_ALBUM)
    }

    fun getClothesPath(index: Int) : String{
        val preDomain = AssetsKey.TRENDING_ASSET
        return "$preDomain/$index.png"
    }
}