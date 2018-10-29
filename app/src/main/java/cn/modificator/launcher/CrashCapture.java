package cn.modificator.launcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashCapture implements Thread.UncaughtExceptionHandler {
  private static final String TAG = "CrashHandler";
  private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
  private static CrashCapture INSTANCE = new CrashCapture();// CrashHandler实例
  private Context mContext;// 程序的Context对象
  private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
  private SimpleDateFormat format = new SimpleDateFormat(
      "yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
  //重启APP时间
  private long mRestartTime;

  // 重启后跳转的Activity
  private Class mRestartActivity;

  boolean isBackground = false;

  private String logFile;

  /**
   * 保证只有一个CrashHandler实例
   */
  private CrashCapture() {

  }

  /**
   * 获取CrashHandler实例 ,单例模式
   */
  public static CrashCapture getInstance() {
    return INSTANCE;
  }

  /**
   * 初始化
   *
   * @param context
   */
  public void init(Context context, long restartTime, Class restartActivity) {
    mContext = context;
    mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
    mRestartTime = restartTime;
    mRestartActivity = restartActivity;
    Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
  }

  /**
   * 当UncaughtException发生时会转入该重写的方法来处理
   */
  @Override
  public void uncaughtException(Thread thread, Throwable ex) {
    ex.printStackTrace();
    if (!handleException(ex) && mDefaultHandler != null) {
      // 如果自定义的没有处理则让系统默认的异常处理器来处理
      mDefaultHandler.uncaughtException(thread, ex);
    } else {
      try {
        Thread.sleep(2000);// 如果处理了，让程序继续运行2秒再退出，保证文件保存并上传到服务器
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      Intent crashIntent = new Intent(mContext, CrashDetailPage.class);
      crashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      if (!TextUtils.isEmpty(logFile)) {
        crashIntent.putExtra("crashFile", logFile);
      }
      mContext.startActivity(crashIntent);

      /*if (!isBackground) {
        Intent intent = new Intent(mContext.getApplicationContext(), mRestartActivity);
        AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        //重启应用，得使用PendingIntent
        PendingIntent restartIntent = PendingIntent.getActivity(
            mContext.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        mAlarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + mRestartTime,
            restartIntent); // 重启应用
      }
      // 退出程序*/

      android.os.Process.killProcess(android.os.Process.myPid());
      System.exit(10);

    }

  }

  /**
   * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
   *
   * @param ex 异常信息
   * @return true 如果处理了该异常信息;否则返回false.
   */
  public boolean handleException(Throwable ex) {
    if (ex == null)
      return false;
    // 收集设备参数信息
    collectDeviceInfo(mContext);
    // 保存日志文件
    logFile = saveCrashInfo2File(ex);
    return true;
  }

  /**
   * 收集设备参数信息
   *
   * @param context
   */
  public void collectDeviceInfo(Context context) {
    try {
      PackageManager pm = context.getPackageManager();// 获得包管理器
      PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
          PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
      if (pi != null) {
        String versionName = pi.versionName == null ? "null"
            : pi.versionName;
        String versionCode = pi.versionCode + "";
        info.put("versionName", versionName);
        info.put("versionCode", versionCode);
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    info.put("osVersion", Build.VERSION.RELEASE);
    info.put("sdkCode", Build.VERSION.SDK_INT + "");
    info.put("FINGERPRINT", Build.FINGERPRINT);
    info.put("DISPLAY", Build.DISPLAY);
   /* Field[] fields = Build.class.getDeclaredFields();// 反射机制
    for (Field field : fields) {
      try {
        field.setAccessible(true);
        if (field.get("") instanceof String[]){
          info.put(field.getName(), Arrays.toString((String[])field.get("")));
        }else {
          info.put(field.getName(), field.get("").toString());
        }
        Log.d(TAG, field.getName() + ":" + field.get(""));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }*/
  }

  private String saveCrashInfo2File(Throwable ex) {
    StringBuffer sb = new StringBuffer();
    for (Map.Entry<String, String> entry : info.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      sb.append(key + "=" + value + "\r\n");
    }
    Writer writer = new StringWriter();
    PrintWriter pw = new PrintWriter(writer);
    ex.printStackTrace(pw);
    Throwable cause = ex.getCause();
    // 循环着把所有的异常信息写入writer中
    while (cause != null) {
      cause.printStackTrace(pw);
      cause = cause.getCause();
    }
    pw.close();// 记得关闭
    String result = writer.toString();
    sb.append(result);

    // 保存文件
    long timetamp = System.currentTimeMillis();
    String time = format.format(new Date());
    String fileName = "crash-" + BuildConfig.VERSION_NAME + "-" + Build.DEVICE + "-" + Build.PRODUCT + "-" + Build.TYPE + "-" + time + "-" + System.currentTimeMillis() + ".log";
    if (Environment.getExternalStorageState().equals(
        Environment.MEDIA_MOUNTED)) {
      try {
//                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
        File dir = mContext.getExternalFilesDir("crash");
        Log.i("CrashHandler", dir.toString());
        if (!dir.exists())
          dir.mkdir();
        FileOutputStream fos = new FileOutputStream(new File(dir,
            fileName));
        fos.write(sb.toString().getBytes());
        fos.close();
        return fileName;
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

}