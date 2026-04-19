package com.conchoback.haingon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes_saved")
data class ClothesSaved(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val typeClothes: String,
    val thumbnail: String
)