-keep,allowshrinking,allowoptimization class cn.modificator.launcher.** {
  *;
}

-keep class cn.modificator.launcher.BaseRecyclerViewFastScrollBar {
  public void setThumbWidth(int);
  public int getThumbWidth();
  public void setTrackWidth(int);
  public int getTrackWidth();
}

-keep class cn.modificator.launcher.BaseRecyclerViewFastScrollPopup {
  public void setAlpha(float);
  public float getAlpha();
}

-keep class cn.modificator.launcher.ButtonDropTarget {
  public int getTextColor();
}

-keep class cn.modificator.launcher.CellLayout {
  public float getBackgroundAlpha();
  public void setBackgroundAlpha(float);
}

-keep class cn.modificator.launcher.CellLayout$LayoutParams {
  public void setWidth(int);
  public int getWidth();
  public void setHeight(int);
  public int getHeight();
  public void setX(int);
  public int getX();
  public void setY(int);
  public int getY();
}

-keep class cn.modificator.launcher.dragndrop.DragLayer$LayoutParams {
  public void setWidth(int);
  public int getWidth();
  public void setHeight(int);
  public int getHeight();
  public void setX(int);
  public int getX();
  public void setY(int);
  public int getY();
}

-keep class cn.modificator.launcher.FastBitmapDrawable {
  public void setDesaturation(float);
  public float getDesaturation();
  public void setBrightness(float);
  public float getBrightness();
}

-keep class cn.modificator.launcher.PreloadIconDrawable {
  public float getAnimationProgress();
  public void setAnimationProgress(float);
}

-keep class cn.modificator.launcher.pageindicators.CaretDrawable {
  public float getCaretProgress();
  public void setCaretProgress(float);
}

-keep class cn.modificator.launcher.Workspace {
  public float getBackgroundAlpha();
  public void setBackgroundAlpha(float);
}

-keep class com.google.android.libraries.launcherclient.* {
  *;
}

-keep,allowshrinking,allowoptimization class me.jfenn.attribouter.** {
 *;
}

-dontwarn javax.**
-dontwarn org.codehaus.mojo.animal_sniffer.**

-keep class cn.modificator.launcher.DeferredHandler {
 *;
}

# Proguard will strip new callbacks in LauncherApps.Callback from
# WrappedCallback if compiled against an older SDK. Don't let this happen.
-keep class cn.modificator.launcher.compat.** {
  *;
}

-keep class cn.modificator.launcher.HiddenAppsFragment {
  *;
}

-keep class cn.modificator.launcher.preferences.ShortcutBlacklistFragment {
  *;
}