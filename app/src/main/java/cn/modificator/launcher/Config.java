package cn.modificator.launcher;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mod on 16-4-23.
 */
public class Config {
  Context context;
  //列数
  public static int colNum = -1;
  //行数
  public static int rowNum = -1;
  public static float fontSize = -1;
  public static boolean hideDivider = false;
  private String preferencesFileName = "launcherPropertyFile";
  private Set<String> hideApps = new HashSet<>();

  public Config(Context context) {
    this.context = context;
  }

  public int getColNum() {
    if (colNum == -1) {
      SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
      colNum = preferences.getInt(Launcher.COL_NUM_KEY, 5);
    }
    return colNum;
  }

  public void setColNum(int colNum) {
    if (this.colNum == colNum)
      return;
    this.colNum = colNum;
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putInt(Launcher.COL_NUM_KEY, colNum).apply();

  }

  public int getRowNum() {
    if (rowNum == -1) {
      SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
      rowNum = preferences.getInt(Launcher.ROW_NUM_KEY, 5);
    }
    return rowNum;
  }

  public void setRowNum(int rowNum) {
    if (this.rowNum == rowNum)
      return;
    this.rowNum = rowNum;
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putInt(Launcher.ROW_NUM_KEY, rowNum).apply();
  }

  public void addHideApp(String packageName) {
    hideApps.add(packageName);
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putStringSet(Launcher.HIDE_APPS_KEY, hideApps).apply();
  }

  public void removeHideApp(String packageName) {
    hideApps.remove(packageName);
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putStringSet(Launcher.HIDE_APPS_KEY, hideApps).apply();
  }

  public void setHideApps(Set<String> hideApps) {
    this.hideApps.clear();
    this.hideApps.addAll(hideApps);
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putStringSet(Launcher.HIDE_APPS_KEY, this.hideApps).apply();
  }

  public Set<String> getHideApps() {
    if (hideApps.isEmpty()) {
      hideApps.addAll(context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE).getStringSet(Launcher.HIDE_APPS_KEY, new HashSet<String>()));
    }
    return hideApps;
  }

  public float getFontSize() {
    if (fontSize == -1) {
      fontSize = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE).getFloat(Launcher.LAUNCHER_FONT_SIZE, 14);
    }
    return fontSize;
  }

  public void saveFontSize() {
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putFloat(Launcher.LAUNCHER_FONT_SIZE, fontSize).apply();
  }

  public boolean getDividerHideStatus() {
    return hideDivider = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE).getBoolean(Launcher.LAUNCHER_HIDE_DIVIDER, true);
  }

  public void setDividerHideStatus(boolean b) {
    SharedPreferences preferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
    preferences.edit().putBoolean(Launcher.LAUNCHER_HIDE_DIVIDER, b).apply();
  }
}
