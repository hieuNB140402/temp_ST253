package com.conchoback.haingon.core.utils.state

sealed class CallApiState<out T> {
    object Loading : CallApiState<Nothing>()
    data class Error(val e: String) : CallApiState<Nothing>()
    data class Success<out T>(val model: T) : CallApiState<T>()
}