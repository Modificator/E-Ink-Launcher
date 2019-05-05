package cn.modificator.launcher;

import android.app.Application;

import cn.modificator.launcher.preferences.PreferenceImpl;
import cn.modificator.launcher.preferences.PreferenceProvider;

public class App extends Application {

     @Override
    public void onCreate() {
        super.onCreate();

        PreferenceProvider.INSTANCE.init(new PreferenceImpl(this));
    }
}
