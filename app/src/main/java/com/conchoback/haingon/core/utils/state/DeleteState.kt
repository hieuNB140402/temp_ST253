package com.conchoback.haingon.core.utils.state

sealed class DeleteState {
    object Empty : DeleteState()
    object Success : DeleteState()
    data class Failure(val error: String?) : DeleteState()
}