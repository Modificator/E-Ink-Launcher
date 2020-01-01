package cn.modificator.launcher;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.modificator.launcher.ftpservice.FTPReceiver;
import cn.modificator.launcher.ftpservice.FTPService;
import cn.modificator.launcher.model.AdminReceiver;
import cn.modificator.launcher.model.AppDataCenter;
import cn.modificator.launcher.model.WifiControl;
import cn.modificator.launcher.widgets.BatteryView;
import cn.modificator.launcher.widgets.EInkLauncherView;

/**
 * Created by mod on 16-4-22.
 */
public class Launcher extends Activity {

  public static final String ROW_NUM_KEY = "rowNumKey";
  public static final String COL_NUM_KEY = "colNumKey";
  public static final String HIDE_APPS_KEY = "hideAppsKey";
  public static final String DELETEAPP = "deleteApp";
  public static final String LAUNCHER_SHOW_STATUS_BAR = "launcherShowStatusBar";
  public static final String LAUNCHER_SHOW_CUSTOM_ICON= "launcherShowCustomIcon";
  public static final String LAUNCHER_ACTION = "launcherReceiver";
  public static final String LAUNCHER_FONT_SIZE = "launcherFontSize";
  public static final String LAUNCHER_HIDE_DIVIDER = "launcherHideDivider";

  EInkLauncherView launcherView;
  AppDataCenter dataCenter = null;
  Config config;
  LauncherUpdateReceiver updateReceiver;
  TextView pageStatus;
  BatteryView batteryProgress;
  TextView batteryStatus, textClock;

  BroadcastReceiver timeListener;
  Calendar mCalendar;

  DevicePolicyManager policyManager;
  File iconFile;
  boolean isChina = true;
  FTPReceiver ftpReceiver = new FTPReceiver();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.launcher_activity);
    config = new Config(this);
    WifiControl.init(this);
    toggleStatusBar();
    if (getExternalCacheDir() != null) {
      iconFile = new File(getExternalCacheDir().getParentFile().getParentFile().getParentFile().getParentFile(), "E-Ink Launcher" + File.separator + "icon");
      if (!iconFile.exists()) {
        iconFile.mkdir();
      }
    }

    isChina = getResources().getConfiguration().locale.getCountry().equals("CN");

    initView();
    //Log.e("----",Arrays.toString(iconFile.list()));
  }


  private void initView() {
    policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    launcherView = findViewById(R.id.mList);
    pageStatus = findViewById(R.id.pageStatus);
    batteryProgress = findViewById(R.id.batteryProgress);
    batteryStatus = findViewById(R.id.batteryStatus);
    textClock = findViewById(R.id.textClock);
    (this.<ImageView>findViewById(R.id.toSetting)).setImageDrawable(Utils.tintDrawable(getResources().getDrawable(R.drawable.navibar_icon_settings_highlight), ColorStateList.valueOf(0xff000000)));

//        launcherView.setIconReplaceFile(Arrays.asList(iconFile.list()));
    launcherView.setHideAppPkg(config.getHideApps());
    launcherView.setHideDivider(config.getDividerHideStatus());
    launcherView.setFontSize(config.getFontSize());

    dataCenter = new AppDataCenter(this);
    dataCenter.setHideApps(config.getHideApps());

    dataCenter.setPageStatus(pageStatus);
    dataCenter.setLauncherView(launcherView);
    //加载之前保存的桌面数据
    updateColNum(config.getColNum());
    updateRowNum(config.getRowNum());

    findViewById(R.id.lastPage).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dataCenter.showLastPage();
      }
    });
    findViewById(R.id.nextPage).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dataCenter.showNextPage();
      }
    });
    findViewById(R.id.toSetting).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingFramgent())
            .addToBackStack(null)
            .commit();
      }
    });
    findViewById(R.id.deleteFinish).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launcherView.setDelete(false);
        dataCenter.refreshAppList();
        config.setHideApps(dataCenter.getHideApps());
        v.setVisibility(View.GONE);
      }
    });
    launcherView.setTouchListener(new EInkLauncherView.TouchListener() {
      @Override
      public void toNext() {
        dataCenter.showNextPage();
      }

      @Override
      public void toLast() {
        dataCenter.showLastPage();
      }
    });
//        android:format12Hour="yyyy-MM-dd aahh:mm EEEE"
//        android:format24Hour="yyyy-MM-dd aahh:mm EEEE"

    mCalendar = Calendar.getInstance();

    updateTimeShow();

    updateReceiver = new LauncherUpdateReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(LAUNCHER_ACTION);
    registerReceiver(updateReceiver, filter);


    IntentFilter appChangeFilter = new IntentFilter();
    appChangeFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
    appChangeFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
    appChangeFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
    appChangeFilter.addDataScheme("package");
    registerReceiver(appChangeReceiver, appChangeFilter);

    try {
      launcherView.setSystemApp(!isUserApp(getPackageManager().getPackageInfo(getPackageName(), 0)));
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void updateRowNum(int rowNum) {
    launcherView.setRowNum(rowNum);
    dataCenter.setRowNum(rowNum);
    config.setRowNum(rowNum);
  }

  private void updateColNum(int colNum) {
    launcherView.setColNum(colNum);
    dataCenter.setColNum(colNum);
    config.setColNum(colNum);
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_PAGE_UP) {
      dataCenter.showLastPage();
    } else if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
      dataCenter.showNextPage();
    } else if (keyCode == KeyEvent.KEYCODE_BACK) {
      return true;
    }
    return super.onKeyUp(keyCode, event);
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    timeListener = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        updateTimeShow();
      }
    };
    updateTimeShow();

    registerReceiver(timeListener, new IntentFilter(Intent.ACTION_TIME_TICK));
    if (launcherView != null) launcherView.refreshReplaceIcon();
    detectionUSB();

    IntentFilter ftpIntentFilter = new IntentFilter(FTPService.ACTION_START_FTPSERVER);
    ftpIntentFilter.addAction(FTPService.ACTION_STOP_FTPSERVER);
    registerReceiver(ftpReceiver,ftpIntentFilter);
  }

  /**
   * 刷新时间显示
   */
  private void updateTimeShow() {
    if (textClock != null && mCalendar != null) {
      boolean is24Hour = DateFormat.is24HourFormat(this);
      mCalendar.setTimeInMillis(System.currentTimeMillis());

      StringBuilder timeFormatTextBuilder = new StringBuilder("yyyy-MM-dd ");
      if (!is24Hour && isChina) {
        timeFormatTextBuilder.append(Utils.getAMPMCNString(mCalendar.get(Calendar.HOUR), mCalendar.get(Calendar.AM_PM)));
      }
      if (is24Hour) {
        timeFormatTextBuilder.append("HH:mm");
      } else {
        timeFormatTextBuilder.append("hh:mm");
      }
      if (!is24Hour && !isChina) {
        timeFormatTextBuilder.append(" a");
      }
      timeFormatTextBuilder.append(" EEEE");
      textClock.setText(new SimpleDateFormat(timeFormatTextBuilder.toString(), Locale.getDefault()).format(mCalendar.getTime()));
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    unregisterReceiver(batteryLevelRcvr);
    unregisterReceiver(timeListener);
    unregisterReceiver(usbReceiver);
    unregisterReceiver(ftpReceiver);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(updateReceiver);
    unregisterReceiver(appChangeReceiver);
    try {
      unregisterReceiver(usbReceiver);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }


  class LauncherUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Bundle bundle = intent.getExtras();
      if (bundle.containsKey(ROW_NUM_KEY)) {
        updateRowNum(bundle.getInt(ROW_NUM_KEY));
      } else if (bundle.containsKey(COL_NUM_KEY)) {
        updateColNum(bundle.getInt(COL_NUM_KEY));
      } else if (bundle.containsKey(DELETEAPP)) {
        launcherView.setDelete(true);

        //显示所有App
        dataCenter.refreshAppList(true);
        findViewById(R.id.deleteFinish).setVisibility(View.VISIBLE);
      } else if (bundle.containsKey(LAUNCHER_FONT_SIZE)) {
        launcherView.setFontSize(bundle.getFloat(LAUNCHER_FONT_SIZE));
      } else if (bundle.containsKey(LAUNCHER_HIDE_DIVIDER)) {
        launcherView.setHideDivider(bundle.getBoolean(LAUNCHER_HIDE_DIVIDER));
        config.setDividerHideStatus(bundle.getBoolean(LAUNCHER_HIDE_DIVIDER));
      }else if (bundle.containsKey(LAUNCHER_SHOW_STATUS_BAR)){
        config.setStatusBarShowStatus(bundle.getBoolean(LAUNCHER_SHOW_STATUS_BAR));
        toggleStatusBar();
      }else if (bundle.containsKey(LAUNCHER_SHOW_CUSTOM_ICON)){
        config.setCustomIconShowStatus(bundle.getBoolean(LAUNCHER_SHOW_CUSTOM_ICON));
        launcherView.refreshReplaceIcon();
      }
    }
  }

  BroadcastReceiver appChangeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      dataCenter.refreshAppList(launcherView.isDelete());
    }
  };
  BroadcastReceiver batteryLevelRcvr = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      int rawlevel = intent.getIntExtra("level", -1);
      int scale = intent.getIntExtra("scale", -1);
      int status = intent.getIntExtra("status", -1);
      int health = intent.getIntExtra("health", -1);
      int level = -1; // percentage, or -1 for unknown
      if (rawlevel >= 0 && scale > 0) {
        level = (rawlevel * 100) / scale;
      }
      batteryProgress.setProgress(level);

      batteryStatus.setVisibility(View.VISIBLE);
      if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
        batteryStatus.setText(R.string.battery_heat);
      } else {
        switch (status) {
          case BatteryManager.BATTERY_STATUS_UNKNOWN:
            batteryStatus.setText(R.string.battery_unknown);
            break;
          case BatteryManager.BATTERY_STATUS_CHARGING:
            batteryStatus.setText(R.string.battery_charging);
                        /*if (level <= 33)
                            sb.append(" is charging, battery level is low"
                                    + "[" + level + "]");
                        else if (level <= 84)
                            sb.append(" is charging." + "[" + level + "]");
                        else
                            sb.append(" will be fully charged.");*/
            break;
          case BatteryManager.BATTERY_STATUS_DISCHARGING:
          case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        /*if (level == 0)
                            sb.append(" needs charging right away.");
                        else if (level > 0 && level <= 33)
                            sb.append(" is about ready to be recharged, battery level is low"
                                    + "[" + level + "]");
                        else
                            sb.append("'s battery level is" + "[" + level + "]");*/
            if (level < 15) {
              batteryStatus.setText(R.string.battery_low);
            } else {
              batteryStatus.setVisibility(View.GONE);
            }
            break;
          case BatteryManager.BATTERY_STATUS_FULL:
//                        sb.append(" is fully charged.");
            batteryStatus.setText(R.string.battery_full);
            break;
          default:
//                        sb.append("'s battery is indescribable!");
            batteryStatus.setText(R.string.battery_wtf);
            break;
        }
      }

    }
  };

  private void detectionUSB() {
    IntentFilter usbFilter = new IntentFilter();
    usbFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
    usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
    usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
    usbFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
    usbFilter.addDataScheme("file");
    registerReceiver(usbReceiver, usbFilter);
  }

  private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(Intent.ACTION_MEDIA_REMOVED)
          || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
        //设备卸载成功;
      } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
        //设备挂载成功
        launcherView.refreshReplaceIcon();
      }
    }
  };

  @Override
  public void onBackPressed() {
    if (getFragmentManager().getBackStackEntryCount() > 0) {
      super.onBackPressed();
      config.saveFontSize();
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && getFragmentManager().getBackStackEntryCount() == 0) {
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  public void lockScreen() {
    if (policyManager.isAdminActive(new ComponentName(this, AdminReceiver.class))) {
      policyManager.lockNow();
    } else {
      activeManage();
    }
  }

  private void activeManage() {
    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(this, AdminReceiver.class));
    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "E-Ink Launcher 获取锁屏权限");
    startActivity(intent);
//    startActivityForResult(intent, 10001);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case 10001:
          policyManager.lockNow();
          break;
      }
    }
  }

  public boolean isSystemApp(PackageInfo pInfo) {
    return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
  }

  public boolean isSystemUpdateApp(PackageInfo pInfo) {
    return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
  }

  public boolean isUserApp(PackageInfo pInfo) {
    return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
  }

  public void toggleStatusBar(){
    int windowFlags= WindowManager.LayoutParams.FLAG_FULLSCREEN;
    if (Config.showStatusBar){
      getWindow().setFlags(windowFlags,windowFlags);
    }else{
      getWindow().clearFlags(windowFlags);
    }
  }
}
