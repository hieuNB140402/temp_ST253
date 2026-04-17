package com.conchoback.haingon

import android.app.Application
import com.conchoback.haingon.core.helper.SharePreferenceHelper
import com.conchoback.haingon.data.model.clothes.SkinModel

class App : Application() {
    val sharePreference by lazy { SharePreferenceHelper(this) }
    val advancedSkinList = ArrayList<SkinModel>()
    val basicSkinList = ArrayList<SkinModel>()

    fun reupdateAdvancedSkinList(list: List<SkinModel>){
        advancedSkinList.clear()
        advancedSkinList.addAll(list)
    }

    fun reupdateBasicSkinList(list: List<SkinModel>){
        basicSkinList.clear()
        basicSkinList.addAll(list)
    }

    companion object {
        lateinit var instant : App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instant = this
    }
}