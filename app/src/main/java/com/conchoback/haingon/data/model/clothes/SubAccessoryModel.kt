package com.conchoback.haingon.data.model.clothes

import androidx.room.PrimaryKey

data class SubAccessoryModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1,
    val accessory: AccessoryModel,
    var isSelected: Boolean = false
)