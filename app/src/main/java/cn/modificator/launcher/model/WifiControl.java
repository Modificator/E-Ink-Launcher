package cn.modificator.launcher.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.provider.Settings;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.List;

import cn.modificator.launcher.Config;
import cn.modificator.launcher.R;
import cn.modificator.launcher.widgets.ObserverFontTextView;
import cn.modificator.launcher.widgets.RatioImageView;

/**
 * Created by Modificator
 * time: 16/12/3.下午12:46
 * des:create file and achieve model
 */

public class WifiControl {
  final String wifiOnResName = "E-ink_Launcher.WifiOn";
  final String wifiOffResName = "E-ink_Launcher.WifiOff";

  ObserverFontTextView appName;
  RatioImageView appImage;
  WifiStateReceiver wifiStateReceiver;
  WifiManager wifiManager;
  ConnectivityManager connManager;
  Context mContext;
  int showNameRes;
  int showIconRes;
  String connectWifiName;
  List<String> iconReplacePkg;
  List<File> iconReplaceFile;

  private static WifiControl instance;

  public static void init(Context context){
    instance = new WifiControl(context.getApplicationContext());
  }

  private WifiControl(Context context){
    mContext = context;
    wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    int wifiState = wifiManager.getWifiState();
    switch (wifiState) {
      case WifiManager.WIFI_STATE_DISABLED:
        showNameRes = R.string.wifi_status_off;
        showIconRes = R.drawable.wifi_off;
        break;
      case WifiManager.WIFI_STATE_DISABLING:
        showNameRes = R.string.wifi_status_closing;
        showIconRes = R.drawable.wifi_on;
        break;
      case WifiManager.WIFI_STATE_ENABLING:
        showNameRes = R.string.wifi_status_opening;
        showIconRes = R.drawable.wifi_off;
        break;
      case WifiManager.WIFI_STATE_ENABLED:
        showNameRes = R.string.wifi_status_on;
        showIconRes = R.drawable.wifi_on;
        break;
    }

    wifiStateReceiver = new WifiStateReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    mContext.registerReceiver(wifiStateReceiver, filter);
  }

  public static void bind(View view, List<String> iconReplacePkg, List<File> iconReplaceFile){
    if (view==null){
      instance.appImage=null;
      instance.appName = null;
      return;
    }
    instance.iconReplacePkg = iconReplacePkg;
    instance.iconReplaceFile = iconReplaceFile;
    instance.appName = view.findViewById(R.id.appName);
    instance.appImage = view.findViewById(R.id.appImage);
    instance.updateStatus();
  }

  public static void reloadWifiName(){
    if (instance.showNameRes == R.string.wifi_status_connected && instance.connectWifiName!=null && instance.connectWifiName.contains("unknown ssid")){
      instance.connectWifiName = instance.wifiManager.getConnectionInfo().getSSID().replace("\"","");
      if (!TextUtils.isEmpty(instance.connectWifiName)){
        instance.connectWifiName = "\n"+instance.connectWifiName;
      }
      instance.updateStatus();
    }
  }

  private void updateStatus(){
    if (appName!=null) {
      appName.setText(mContext.getString(showNameRes, connectWifiName));
      if (Config.showCustomIcon && iconReplacePkg != null) {
        String fileName = showIconRes == R.drawable.wifi_on ? wifiOnResName : wifiOffResName;
        appImage.setImageURI(Uri.fromFile(iconReplaceFile.get(iconReplacePkg.indexOf(fileName))));
      } else {
        appImage.setImageResource(showIconRes);
      }
    }
  }


  public static void onClickWifiItem(){
    int wifiApState = instance.wifiManager.getWifiState();  //获取wifi状态

    if (wifiApState == instance.wifiManager.WIFI_STATE_ENABLING || wifiApState == instance.wifiManager.WIFI_STATE_ENABLED)
      instance.wifiManager.setWifiEnabled(false);
    else
      instance.wifiManager.setWifiEnabled(true);
  }

  public static void onLongClickWifiItem(){
    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    instance.mContext.startActivity(intent);
  }

  class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        switch (wifiState) {
          case WifiManager.WIFI_STATE_DISABLED:
            showNameRes = R.string.wifi_status_off;
            showIconRes = R.drawable.wifi_off;
            break;
          case WifiManager.WIFI_STATE_DISABLING:
            showNameRes = R.string.wifi_status_closing;
            showIconRes = R.drawable.wifi_on;
            break;
          case WifiManager.WIFI_STATE_ENABLING:
            showNameRes = R.string.wifi_status_opening;
            showIconRes = R.drawable.wifi_off;
            break;
          case WifiManager.WIFI_STATE_ENABLED:
            showNameRes = R.string.wifi_status_on;
            showIconRes = R.drawable.wifi_on;
            break;
        }
      }
      if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {


        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if (null != parcelableExtra) {
          NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
          switch (networkInfo.getState()) {
            case CONNECTED:
              Log.e("APActivity", "CONNECTED");
//                            appName.setSingleLine(false);
              String wifiName = "";
              if (networkInfo.getExtraInfo()!=null) {
                wifiName = networkInfo.getExtraInfo().replace("\"", "");
              }
              if (wifiName.isEmpty()){
                wifiName = wifiManager.getConnectionInfo().getSSID().replace("\"","");
              }
              if (!TextUtils.isEmpty(wifiName)){
                wifiName = "\n"+wifiName;
              }
              showNameRes= R.string.wifi_status_connected;
              connectWifiName = wifiName;

              break;
            case CONNECTING:
              Log.e("APActivity", "CONNECTING");
              showNameRes = R.string.wifi_status_connecting;
              break;
            case DISCONNECTED:
              Log.e("APActivity", "DISCONNECTED");
              showNameRes = R.string.wifi_status_disconnected;
              break;
            case DISCONNECTING:
              Log.e("APActivity", "DISCONNECTING");
              showNameRes = R.string.wifi_status_disconnecting;
              break;
            case SUSPENDED:
              Log.e("APActivity", "SUSPENDED");
              break;
            case UNKNOWN:
              Log.e("APActivity", "UNKNOWN");
              break;
            default:
              break;
          }
        }
      }
      updateStatus();
    }
  }
}
