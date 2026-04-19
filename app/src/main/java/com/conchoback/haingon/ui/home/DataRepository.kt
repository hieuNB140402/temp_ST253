package com.conchoback.haingon.ui.home

import com.conchoback.haingon.data.local.ClothesSaved
import com.conchoback.haingon.data.local.ClothesSavedDAO
import javax.inject.Inject

class DataRepository @Inject constructor(private val clothesSavedDAO: ClothesSavedDAO) {

    suspend fun getAllClothesSaved(): List<ClothesSaved> {
        return clothesSavedDAO.getAllClothesSaved()
    }

    suspend fun deleteClothesSavedByIds(ids: List<Int>) {
        clothesSavedDAO.deleteClothesSavedByIds(ids)
    }

    suspend fun deleteClothesSavedById(id: Int) {
        clothesSavedDAO.deleteClothesSavedById(id)
    }

    suspend fun insertClothesSavedList(clothesSavedList: List<ClothesSaved>) {
        clothesSavedDAO.insertClothesSavedList(clothesSavedList)
    }
}