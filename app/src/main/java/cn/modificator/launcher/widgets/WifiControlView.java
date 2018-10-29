package cn.modificator.launcher.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;

import androidx.annotation.AttrRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import cn.modificator.launcher.R;

/**
 * Created by Modificator
 * time: 16/12/3.下午12:46
 * des:create file and achieve model
 */

public class WifiControlView extends FrameLayout implements Observer {
  ObserverFontTextView appName;
  RatioImageView appImage;
  WifiStateReceiver wifiStateReceiver;
  WifiManager wifiManager;

  public WifiControlView(@NonNull Context context) {
    super(context);
    init();
  }

  public WifiControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public WifiControlView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    addView(inflate(getContext(), R.layout.launcher_item, null), new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    getChildAt(0).setClickable(false);
    appName = findViewById(R.id.appName);
    appImage = findViewById(R.id.appImage);
    appImage.setImageResource(R.drawable.wifi_off);
    appName.setSingleLine(false);
    appName.setMinLines(2);
    appName.setGravity(Gravity.CENTER);
    setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (wifiManager != null || (wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null) {
          int wifiApState = wifiManager.getWifiState();  //获取wifi状态

          if (wifiApState == wifiManager.WIFI_STATE_ENABLING || wifiApState == wifiManager.WIFI_STATE_ENABLED)
            wifiManager.setWifiEnabled(false);
          else
            wifiManager.setWifiEnabled(true);
        }
      }
    });
    setOnLongClickListener(new OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        //Log.e("-----", "111111");
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        getContext().startActivity(intent);
        return true;
      }
    });
    if (wifiManager != null) {
      int wifiState = wifiManager.getWifiState();
      switch (wifiState) {
        case WifiManager.WIFI_STATE_DISABLED:
          appName.setText(R.string.wifi_status_off);
          appImage.setImageResource(R.drawable.wifi_off);
          break;
        case WifiManager.WIFI_STATE_DISABLING:
          appName.setText(R.string.wifi_status_closing);
          appImage.setImageResource(R.drawable.wifi_on);
          break;
        case WifiManager.WIFI_STATE_ENABLING:
          appName.setText(R.string.wifi_status_opening);
          appImage.setImageResource(R.drawable.wifi_off);
          break;
        case WifiManager.WIFI_STATE_ENABLED:
          appName.setText(R.string.wifi_status_on);
          appImage.setImageResource(R.drawable.wifi_on);
          break;
      }
    }
    wifiStateReceiver = new WifiStateReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    getContext().registerReceiver(wifiStateReceiver, filter);
  }

  @Override
  public void update(Observable o, Object arg) {
    appName.update(o, arg);
  }

  @Override
  protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
       /* if (visibility == VISIBLE) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            getContext().registerReceiver(wifiStateReceiver, filter);
        } else {
            try {
                getContext().unregisterReceiver(wifiStateReceiver);
            }catch (Exception e){e.printStackTrace();}
        }*/
  }

  class WifiStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//            appName.setSingleLine(true);
      Bundle bundle = intent.getExtras();
      if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
        switch (wifiState) {
          case WifiManager.WIFI_STATE_DISABLED:
            appName.setText(R.string.wifi_status_off);
            appImage.setImageResource(R.drawable.wifi_off);
            break;
          case WifiManager.WIFI_STATE_DISABLING:
            appName.setText(R.string.wifi_status_closing);
            appImage.setImageResource(R.drawable.wifi_on);
            break;
          case WifiManager.WIFI_STATE_ENABLING:
            appName.setText(R.string.wifi_status_opening);
            appImage.setImageResource(R.drawable.wifi_off);
            break;
          case WifiManager.WIFI_STATE_ENABLED:
            appName.setText(R.string.wifi_status_on);
            appImage.setImageResource(R.drawable.wifi_on);
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
              appName.setText(getResources().getString(R.string.wifi_status_connected, networkInfo.getExtraInfo().replaceFirst("\"", "\n").replace("\"", "")));
              break;
            case CONNECTING:
              Log.e("APActivity", "CONNECTING");
              appName.setText(R.string.wifi_status_connecting);
              break;
            case DISCONNECTED:
              Log.e("APActivity", "DISCONNECTED");
              appName.setText(R.string.wifi_status_disconnected);
              break;
            case DISCONNECTING:
              Log.e("APActivity", "DISCONNECTING");
              appName.setText(R.string.wifi_status_disconnecting);
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

    }
  }
}
