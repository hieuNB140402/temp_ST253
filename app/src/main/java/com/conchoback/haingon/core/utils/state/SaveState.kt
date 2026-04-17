package com.conchoback.haingon.core.utils.state

sealed class SaveState {
    data class Success(val path: String) : SaveState()
    data class Error(val exception: Exception) : SaveState()
    object Loading : SaveState()
    object Nothing : SaveState()
}