package com.conchoback.haingon.core.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.conchoback.haingon.core.service.ServiceLocator

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

//    override fun <T : ViewModel> create(modelClass: Class<T>): T {

//        return when (modelClass) {
//            AdvancedViewModel::class.java -> { AdvancedViewModel(ServiceLocator.provideSkinRepository(context)) as T }
//            AllDataViewModel::class.java -> { AllDataViewModel(ServiceLocator.provideAllDataRepository(context)) as T }

//            else -> throw IllegalArgumentException("Unknown ViewModel")
//        }
//    }
}