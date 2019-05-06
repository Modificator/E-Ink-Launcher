package cn.modificator.launcher;

import android.os.Looper;

import cn.modificator.launcher.util.LooperExecutor;

public class MainThreadExecutor extends LooperExecutor {

    public MainThreadExecutor() {
        super(Looper.getMainLooper());
    }
}
