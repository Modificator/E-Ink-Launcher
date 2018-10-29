package cn.modificator.launcher;

import android.app.Application;

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    CrashCapture.getInstance().init(this, 1, Launcher.class);
  }


}
