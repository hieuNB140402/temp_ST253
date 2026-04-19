package com.conchoback.haingon

import android.app.Application
import com.conchoback.haingon.core.helper.SharePreferenceHelper
import com.conchoback.haingon.data.model.clothes.SkinModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    val sharePreference by lazy { SharePreferenceHelper(this) }
    val shirtListDefault = ArrayList<String>()
    val pantListDefault = ArrayList<String>()
    val comboListDefault = ArrayList<String>()

    fun reupdateShirtListDefault(list: List<String>) {
        shirtListDefault.clear()
        shirtListDefault.addAll(list)
    }

    fun reupdatePantListDefault(list: List<String>) {
        pantListDefault.clear()
        pantListDefault.addAll(list)
    }

    fun reupdateComboListDefault(list: List<String>) {
        comboListDefault.clear()
        comboListDefault.addAll(list)
    }


    companion object {
        lateinit var instant: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instant = this
    }
}