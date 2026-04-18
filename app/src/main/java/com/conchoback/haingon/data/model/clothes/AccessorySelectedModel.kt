package com.conchoback.haingon.data.model.clothes

data class AccessorySelectedModel(
    val typeAccessory: String,
    val thumbnail: String,
    var isSelected: Boolean = false,
    val subAccessoryList: ArrayList<SubAccessoryModel>,
)