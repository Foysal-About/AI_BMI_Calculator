package com.happylens.ai_bmi_calculator.util

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString()
