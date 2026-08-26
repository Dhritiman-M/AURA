package com.dhritiman.aura.accessibility

sealed class UIAction {

    data class FindText(
        val text: String
    ) : UIAction()

    data class ClickText(
        val text: String
    ) : UIAction()

    data class ClickDescription(
        val description: String
    ) : UIAction()

    data class TypeText(
        val text: String
    ) : UIAction()

    data object PressBack : UIAction()

    data object ScrollDown : UIAction()

    data object ScrollForward : UIAction()

    data object ScrollBackward : UIAction()
}