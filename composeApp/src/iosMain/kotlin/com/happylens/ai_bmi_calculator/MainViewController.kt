package com.happylens.ai_bmi_calculator

import androidx.compose.runtime.disableHotReloadMode
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(): platform.UIKit.UIViewController {
    disableHotReloadMode()
    return ComposeUIViewController {
        App()
    }
}
