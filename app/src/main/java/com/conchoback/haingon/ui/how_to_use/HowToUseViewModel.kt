package com.conchoback.haingon.ui.how_to_use

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HowToUseViewModel : ViewModel() {
    // Flow Declaration
    //==================================================================================================================
    private val _positionSelected = MutableStateFlow(0)
    val positionSelected = _positionSelected.asStateFlow()

    // Normal Declaration
    //==================================================================================================================

    // Getter Setter
    //==================================================================================================================
    fun setNextPosition() {
        _positionSelected.value += 1
    }

    fun setPrePosition() {
        _positionSelected.value -= 1
    }

    fun setPosition(position: Int) {
        _positionSelected.value = position
    }

    // Function feature
    //==================================================================================================================

}