package com.resgrid.plugins.headset

enum class HeadsetTypes(val value: Int) {
    B01(0),
    HYS(1),
    AINA(2);

    companion object {
        fun fromInt(value: Int) = HeadsetTypes.values().first { it.value == value }
    }
}