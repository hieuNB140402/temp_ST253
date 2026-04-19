package com.conchoback.haingon.ui.home

sealed class MyCreationPayload {
    data class SelectedChanged(val isSelected: Boolean) : MyCreationPayload()
    data class ShowSelectChanged(val isShowSelection: Boolean) : MyCreationPayload()
}