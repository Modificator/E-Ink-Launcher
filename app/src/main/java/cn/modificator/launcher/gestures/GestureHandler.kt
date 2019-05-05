package cn.modificator.launcher.gestures

import cn.modificator.launcher.Launcher

abstract class GestureHandler(val launcher: Launcher) {

    abstract fun onGestureTrigger()
}