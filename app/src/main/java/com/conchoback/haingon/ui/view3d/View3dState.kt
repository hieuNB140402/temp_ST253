package com.conchoback.haingon.ui.view3d

import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.clothes.ClothesModel

data class View3dState(
    val typeClothes: String = "",
    val theme: String = "",
    val typeCharacter: String = "",
    val shirt: ClothesModel? = null,
    val pant: ClothesModel? = null,
    val accessories: List<AccessoryModel> = emptyList(),
    val isShowFeature: Boolean = false
)