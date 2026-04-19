package com.conchoback.haingon.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ClothesSavedDAO {
    // Inset
    @Insert
    suspend fun insertClothesSaved(clothesSaved: ClothesSaved)

    @Insert
    suspend fun insertClothesSavedList(list: List<ClothesSaved>)

    // Get
    @Query("SELECT * FROM clothes_saved")
    suspend fun getAllClothesSaved(): List<ClothesSaved>

    @Query("SELECT * FROM clothes_saved WHERE id = :id")
    suspend fun selectClothesSavedById(id: Int): ClothesSaved

    // Update
    @Update
    suspend fun updateClothesSaved(clothesSaved: ClothesSaved)

    // Delete
    @Query("DELETE FROM clothes_saved")
    fun deleteAllClothesSaved()

    @Query("DELETE FROM clothes_saved WHERE id = :id")
    fun deleteClothesSavedById(id: Int)

    @Query("DELETE FROM clothes_saved WHERE id IN (:ids)")
    suspend fun deleteClothesSavedByIds(ids: List<Int>)

}