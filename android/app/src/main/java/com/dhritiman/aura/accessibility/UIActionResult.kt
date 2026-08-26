package com.dhritiman.aura.accessibility

sealed class UIActionResult {

    data object Success : UIActionResult()

    data object NotFound : UIActionResult()

    data object NotClickable : UIActionResult()

    data object NotEditable : UIActionResult()

    data object Failed : UIActionResult()
}