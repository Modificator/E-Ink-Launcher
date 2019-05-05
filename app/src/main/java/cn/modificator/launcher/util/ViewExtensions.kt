package cn.modificator.launcher.util

import android.view.View
import kotlin.contracts.contract

fun View?.hide() {
    if (this == null) {
        return
    }
    visibility = View.GONE
}

fun View?.show() {
    if (this == null) {
        return
    }
    visibility = View.VISIBLE
}