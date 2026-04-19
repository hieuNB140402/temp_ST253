package com.conchoback.haingon.data.model.clothes

import androidx.room.PrimaryKey

data class AccessorySelectedModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1,
    val typeAccessory: String,
    val thumbnail: String,
    var isSelected: Boolean = false,
    var subAccessoryList: List<SubAccessoryModel>,
)