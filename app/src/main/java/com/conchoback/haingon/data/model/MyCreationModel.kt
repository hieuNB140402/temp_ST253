package com.conchoback.haingon.data.model

import androidx.room.PrimaryKey

data class MyCreationModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val clothes: DownloadModel,
    val isShowSelection: Boolean = false,
    val isSelected: Boolean = false
)
