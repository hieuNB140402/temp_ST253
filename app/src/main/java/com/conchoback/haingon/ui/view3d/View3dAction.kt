package com.conchoback.haingon.ui.view3d

import com.conchoback.haingon.data.model.AccessoryModel
import com.conchoback.haingon.data.model.clothes.ClothesModel

sealed class View3dAction {
    data class ChangeTypeClothes(val typeClothes: String) : View3dAction()
    data class ChangeTheme(val theme: String) : View3dAction()
    data class ChangeTypeCharacter(val typeCharacter: String) : View3dAction()

    data class ChangeShirt(val itemShirt: ClothesModel) : View3dAction()
    data class ChangePant(val itemPant: ClothesModel) : View3dAction()

    data class ChangeAccessory(val itemList: List<AccessoryModel>) : View3dAction()

    object ClearAccessory : View3dAction()

    data class ChangeShowFeature(val isShowFeature: Boolean, val typeClothesSelected: String) : View3dAction()
}