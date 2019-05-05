package cn.modificator.launcher.config

import android.content.Context
import cn.modificator.launcher.allapps.theme.IAllAppsThemer
import cn.modificator.launcher.popup.theme.IPopupThemer

interface IThemer {

    fun allAppsTheme(context: Context): IAllAppsThemer
    fun popupTheme(context: Context): IPopupThemer
}