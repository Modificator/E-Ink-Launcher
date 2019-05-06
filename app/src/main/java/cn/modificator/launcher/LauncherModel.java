package cn.modificator.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LauncherModel extends BroadcastReceiver {

    final LauncherAppState mApp;

    public LauncherModel(LauncherAppState app) {
        this.mApp = app;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
