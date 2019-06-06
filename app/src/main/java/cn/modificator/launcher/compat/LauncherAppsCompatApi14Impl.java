package cn.modificator.launcher.compat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;

import java.util.List;

public class LauncherAppsCompatApi14Impl extends LauncherAppsCompat{
    @Override
    public List<LauncherActivityInfo> getActivityList(String packageName, UserHandle user) {
        return null;
    }

    @Override
    public LauncherActivityInfo resolveActivity(Intent intent, UserHandle user) {
        return null;
    }

    @Override
    public void startActivityForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts) {

    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags, UserHandle user) {
        return null;
    }

    @Override
    public void showAppDetailsForProfile(ComponentName component, UserHandle user, Rect sourceBounds, Bundle opts) {

    }

    @Override
    public void addOnAppsChangedCallback(OnAppsChangedCallbackCompat listener) {

    }

    @Override
    public void removeOnAppsChangedCallback(OnAppsChangedCallbackCompat listener) {

    }

    @Override
    public boolean isPackageEnabledForProfile(String packageName, UserHandle user) {
        return false;
    }

    @Override
    public boolean isActivityEnabledForProfile(ComponentName component, UserHandle user) {
        return false;
    }

    @Override
    public List<ShortcutConfigActivityInfo> getCustomShortcutActivityList(PackageUserKey packageUser) {
        return null;
    }
}
