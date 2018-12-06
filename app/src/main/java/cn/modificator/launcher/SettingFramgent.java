package cn.modificator.launcher;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;

import cn.modificator.launcher.ftpservice.FTPService;

/**
 * Created by mod on 16-5-3.
 */
public class SettingFramgent extends Fragment implements View.OnClickListener {
  Spinner col_num_spinner;
  Spinner row_num_spinner;
  SeekBar font_control;
  View rootView;
  TextView hideDivider, ftpAddr, ftpStatus;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.activity_setting, null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    rootView = getView();
    rootView.findViewById(R.id.toBack).setOnClickListener(this);
    rootView.findViewById(R.id.rootView).setOnClickListener(this);
    rootView.findViewById(R.id.deleteApp).setOnClickListener(this);
    rootView.findViewById(R.id.toSort).setOnClickListener(this);    //添加设置界面点击跳转
    ftpStatus = (TextView) rootView.findViewById(R.id.ftp_status);
    ftpAddr = (TextView) rootView.findViewById(R.id.ftp_addr);
    hideDivider = (TextView) rootView.findViewById(R.id.hideDivider);
    hideDivider.setOnClickListener(this);
    hideDivider.setText(Config.hideDivider ? R.string.setting_show_divider : R.string.setting_hide_divider);
    font_control = (SeekBar) rootView.findViewById(R.id.font_control);
    col_num_spinner = (Spinner) rootView.findViewById(R.id.col_num_spinner);
    row_num_spinner = (Spinner) rootView.findViewById(R.id.row_num_spinner);
    row_num_spinner.setSelection(Config.rowNum - 2, false);
    font_control.setProgress((int) ((Config.fontSize - 10) * 10));

    row_num_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(Launcher.ROW_NUM_KEY, position + 2);
        intent.setAction(Launcher.LAUNCHER_ACTION);
        getActivity().sendBroadcast(intent);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    col_num_spinner.setSelection(Config.colNum - 2, false);
    col_num_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(Launcher.COL_NUM_KEY, position + 2);
        intent.setAction(Launcher.LAUNCHER_ACTION);
        getActivity().sendBroadcast(intent);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
    rootView.findViewById(R.id.btnHideFontControl).setOnClickListener(this);
    rootView.findViewById(R.id.changeFontSize).setOnClickListener(this);
    rootView.findViewById(R.id.helpAbout).setOnClickListener(this);
    rootView.findViewById(R.id.menu_ftp).setOnClickListener(this);

    font_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
          Intent intent = new Intent();
          Config.fontSize = 10 + progress / 10f;
          intent.putExtra(Launcher.LAUNCHER_FONT_SIZE, 10 + progress / 10f);
          intent.setAction(Launcher.LAUNCHER_ACTION);
          getActivity().sendBroadcast(intent);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
      }
    });

    updateStatus();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.toBack:
      case R.id.rootView:
        getActivity().onBackPressed();
        break;
      case R.id.deleteApp:
        Intent intent = new Intent();
        intent.putExtra(Launcher.DELETEAPP, true);
        intent.setAction(Launcher.LAUNCHER_ACTION);
        getActivity().sendBroadcast(intent);
        getActivity().onBackPressed();
        break;
      case R.id.helpAbout:
        AboutDialog.getInstance(getActivity()).show();
        break;
      case R.id.btnHideFontControl:
        rootView.findViewById(R.id.menuList).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.font_control_p).setVisibility(View.GONE);
        break;
      case R.id.changeFontSize:
        rootView.findViewById(R.id.menuList).setVisibility(View.GONE);
        rootView.findViewById(R.id.font_control_p).setVisibility(View.VISIBLE);
        break;
      case R.id.hideDivider:
        Config.hideDivider = !Config.hideDivider;
        hideDivider.setText(Config.hideDivider ? "显示分隔线" : "隐藏分隔线");

        intent = new Intent();
        intent.putExtra(Launcher.LAUNCHER_HIDE_DIVIDER, Config.hideDivider);
        intent.setAction(Launcher.LAUNCHER_ACTION);
        getActivity().sendBroadcast(intent);
        getActivity().onBackPressed();
        break;
      case R.id.menu_ftp:
        if (!FTPService.isRunning()) {
          if (FTPService.isConnectedToWifi(getActivity()))
            startServer();
          else
            Toast.makeText(getActivity(), "大哥诶，麻烦先把WIFI连上吧", Toast.LENGTH_SHORT).show();
        } else {
          stopServer();
        }
        break;
      case R.id.toSort:
          intent = new Intent(v.getContext(), SortActivity.class);
          startActivity(intent);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    updateStatus();
    IntentFilter wifiFilter = new IntentFilter();
    wifiFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    getActivity().registerReceiver(mWifiReceiver, wifiFilter);
    IntentFilter ftpFilter = new IntentFilter();
    ftpFilter.addAction(FTPService.ACTION_STARTED);
    ftpFilter.addAction(FTPService.ACTION_STOPPED);
    ftpFilter.addAction(FTPService.ACTION_FAILEDTOSTART);
    getActivity().registerReceiver(ftpReceiver, ftpFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mWifiReceiver);
    getActivity().unregisterReceiver(ftpReceiver);
  }


  /**
   * Sends a broadcast to start ftp server
   */
  private void startServer() {
    getActivity().sendBroadcast(new Intent(FTPService.ACTION_START_FTPSERVER));
  }

  /**
   * Sends a broadcast to stop ftp server
   */
  private void stopServer() {
    getActivity().sendBroadcast(new Intent(FTPService.ACTION_STOP_FTPSERVER));
  }

  /**
   * Update UI widgets based on connection status
   */
  private void updateStatus() {
    if (FTPService.isConnectedToWifi(getActivity())) {
      if (FTPService.isRunning()) {
//                ftpAddr.setText("网络传书 （开）");
        ftpStatus.setText(R.string.setting_cloud_manager_on);
        ftpAddr.setVisibility(View.VISIBLE);
        ftpAddr.setText(getFTPAddressString());
      } else {
//                ftpAddr.setText("网络传书 （关）");
        ftpStatus.setText(R.string.setting_cloud_manager_off);
        ftpAddr.setVisibility(View.GONE);
      }
    } else {
//            ftpAddr.setText("网络传书 （请连接WIFI）");
      ftpStatus.setText(R.string.setting_cloud_manager_wifi_off);
      ftpAddr.setVisibility(View.GONE);
    }
  }

  /**
   * @return address at which server is running
   */
  private String getFTPAddressString() {
    return "ftp://" + FTPService.getLocalInetAddress(getActivity()).getHostAddress() + ":" + FTPService.getPort();
  }


  private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
      ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = conMan.getActiveNetworkInfo();
      if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {

      } else {
        stopServer();
      }
      updateStatus();
    }
  };
  private BroadcastReceiver ftpReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      updateStatus();
      if (action == FTPService.ACTION_STARTED) {
//                statusText.setText(getResources().getString(R.string.ftp_status_running));
//                warningText.setText("");
//                ftpAddrText.setText(getFTPAddressString());
//                ftpBtn.setText(getResources().getString(R.string.stop_ftp));
      } else if (action == FTPService.ACTION_FAILEDTOSTART) {
//                statusText.setText(getResources().getString(R.string.ftp_status_not_running));
//                warningText.setText("Oops! Something went wrong");
//                ftpAddrText.setText("");
//                ftpBtn.setText(getResources().getString(R.string.start_ftp));
      } else if (action == FTPService.ACTION_STOPPED) {
//                statusText.setText(getResources().getString(R.string.ftp_status_not_running));
//                ftpAddrText.setText("");
//                ftpBtn.setText(getResources().getString(R.string.start_ftp));
      }
    }
  };
}
