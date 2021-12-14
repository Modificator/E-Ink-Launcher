package cn.modificator.launcher.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;
import java.util.Set;

import cn.modificator.launcher.Config;
import cn.modificator.launcher.Launcher;
import cn.modificator.launcher.R;
import cn.modificator.launcher.model.AppDataCenter;
import cn.modificator.launcher.model.ObservableFloat;
import cn.modificator.launcher.model.WifiControl;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * Created by mod on 16-4-23.
 */
public class EInkLauncherView extends ViewGroup{

  int ROW_NUM = 5;
  int COL_NUM = 5;
  float dragDistance = 0;
  private List<ResolveInfo> dataList = new ArrayList<>();
  PackageManager packageManager;
  TouchListener touchListener;
  boolean isDelete = false;
  float fontSize = 14;
  ObservableFloat observable = new ObservableFloat();
  boolean isSystemApp = false;
  Set<String> hideAppPkg = new HashSet<>();
  OnSingleAppHideChange onSingleAppHideChange;
  List<File> iconReplaceFile = new ArrayList<>();
  List<String> iconReplacePkg = new ArrayList<>();
  boolean hideDivider = false;
//    GestureDetector gestureDetector;

  public EInkLauncherView(Context context) {
    super(context);
    init();
  }

  public EInkLauncherView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public EInkLauncherView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  public void setHideAppPkg(Set<String> hideAppPkg) {
    this.hideAppPkg.clear();
    this.hideAppPkg.addAll(hideAppPkg);
  }

  public void setHideDivider(boolean hideDivider) {
    this.hideDivider = hideDivider;
    resetIconLayout();
  }

  public Set<String> getHideAppPkg() {
    return hideAppPkg;
  }

  public void refreshReplaceIcon() {
    this.iconReplaceFile.clear();
    this.iconReplacePkg.clear();
    if (getContext().getExternalCacheDir() == null) return;
    if (!Config.showCustomIcon) {
      File iconFileRoot = null;
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        iconFileRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "E-Ink Launcher" + File.separator + "icon");
      }else{
        iconFileRoot = new File(Environment.getExternalStorageDirectory(),"E-Ink Launcher" + File.separator + "icon");
      }
      if (!iconFileRoot.exists()){
        try {
          iconFileRoot.mkdirs();
        }catch (Exception e){}
      }
      if (iconFileRoot == null || iconFileRoot.listFiles() == null) return;
      for (File file : iconFileRoot.listFiles()) {
        this.iconReplaceFile.add(file);
        this.iconReplacePkg.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
      }
    }
    refreshIconData();
//        this.iconReplaceFile.addAll(iconReplaceFile);
  }

  private void init() {
    dragDistance = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 6f;
    packageManager = getContext().getPackageManager();

//    gestureDetector = new GestureDetector(getContext(), onGestureListener);
  }

  public void setTouchListener(TouchListener touchListener) {
    this.touchListener = touchListener;
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    dragDistance = Math.min(getMeasuredWidth(), getMeasuredHeight()) / 6f;

    for (int i = 0; i < ROW_NUM; i++) {
      for (int j = 0; j < COL_NUM; j++) {
                /*if (COL_NUM * i + j == dataList.size())
                    break AddView;*/
        int childLeft = j * getItemWidth();// + (j * dividerSize);
        int childRight = (j + 1) * getItemWidth();// + (j * dividerSize);
        int childTop = i * getItemHeight();// + (i * dividerSize);
        int childBottom = (i + 1) * getItemHeight();// + (i * dividerSize);
//                view.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        getChildAt(i*COL_NUM+j).layout(childLeft, childTop, childRight, childBottom);
      }
    }
    if (getChildCount()>0){
      if (getChildAt(0).findViewById(R.id.appName).getMeasuredWidth() ==0) {
        refreshIconData();
      }
    }
  }

  private void resetIconLayout(){
    observable.deleteObservers();
    removeAllViews();
    for (int i = 0; i < ROW_NUM * COL_NUM; i++) {
      View itemView = LayoutInflater.from(getContext()).inflate(R.layout.launcher_item, this, false);
      observable.addObserver((Observer) itemView.findViewById(R.id.appName));
      TextView tvAppName = ((TextView)itemView.findViewById(R.id.appName));
      tvAppName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Config.fontSize);
      tvAppName.setMinLines(Config.appNameLines==2?Config.appNameLines:0);
      tvAppName.setMaxLines(Config.appNameLines);
      tvAppName.setEllipsize(TextUtils.TruncateAt.END);

      if (hideDivider) {
        itemView.setBackgroundResource(R.drawable.app_item_final);
        } else if (i==ROW_NUM*COL_NUM-1) {
        itemView.setBackgroundResource(R.drawable.app_item_final);
      }else if (i%COL_NUM==COL_NUM-1){
        itemView.setBackgroundResource(R.drawable.app_item_right);
      }else if (i>(ROW_NUM-1)*COL_NUM-1){
        itemView.setBackgroundResource(R.drawable.app_item_bottom);
      } else{
        itemView.setBackgroundResource(R.drawable.app_item_normal);
      }
//      if (COL_NUM * i + j < dataList.size() + 2) {
//        if (hideDivider) {
//          view.setBackgroundResource(R.drawable.app_item_final);
//        } else if (j == COL_NUM - 1 && i == ROW_NUM - 1) {
//          view.setBackgroundResource(R.drawable.app_item_final);
//        } else if (j == COL_NUM - 1)
//          view.setBackgroundResource(R.drawable.app_item_right);
//        else if (i == ROW_NUM - 1)
//          view.setBackgroundResource(R.drawable.app_item_bottom);
//        else if (!hideDivider)
//          view.setBackgroundResource(R.drawable.app_item_normal);
//      }
      addView(itemView);
    }
    refreshIconData();
  }

  private void refreshIconData(){
    int position;
    View itemView;
    WifiControl.bind(null, iconReplacePkg, iconReplaceFile);
    for (int i = 0; i < ROW_NUM; i++) {
      for (int j = 0; j < COL_NUM; j++) {
        position = i*COL_NUM+j;
        itemView = getChildAt(position);
        if (itemView == null) {
          return;
        }
        if (position<dataList.size()&&position<getChildCount()) {
          String packageName = dataList.get(COL_NUM * i + j).activityInfo.packageName;
          if (AppDataCenter.wifiPackageName.equals(packageName)){
            WifiControl.bind(itemView, iconReplacePkg,iconReplaceFile);
          }else if (AppDataCenter.oneKeyLockPackageName.equals(packageName)){
            if (iconReplacePkg.contains(packageName)) {
              ((ImageView) itemView.findViewById(R.id.appImage)).setImageURI(Uri.fromFile(iconReplaceFile.get(iconReplacePkg.indexOf(packageName))));
            }else {
              ((ImageView) itemView.findViewById(R.id.appImage)).setImageResource(R.drawable.ic_onekeylock);
            }
            ((TextView) itemView.findViewById(R.id.appName)).setText(R.string.item_lockscreen);
          }else{
            if (iconReplacePkg.contains(packageName)) {
              ((ImageView) itemView.findViewById(R.id.appImage)).setImageURI(Uri.fromFile(iconReplaceFile.get(iconReplacePkg.indexOf(packageName))));
            } else {
              ((ImageView) itemView.findViewById(R.id.appImage)).setImageDrawable(dataList.get(COL_NUM * i + j).loadIcon(packageManager));
            }
            ((TextView) itemView.findViewById(R.id.appName)).setText(dataList.get(COL_NUM * i + j).loadLabel(packageManager));
          }
          itemView.setOnClickListener(new ItemClickListener(COL_NUM * i + j));
          itemView.setOnLongClickListener(new ItemLongClickListener(COL_NUM * i + j));
          itemView.findViewById(R.id.menu_delete).setOnClickListener(new ItemClickListener(COL_NUM * i + j));
          itemView.findViewById(R.id.menu_hide).setOnClickListener(new ItemHideClickListener(COL_NUM * i + j));
          itemView.setVisibility(VISIBLE);
          itemView.setAlpha(1);
        }else{
          ((TextView) itemView.findViewById(R.id.appName)).setText("");
          ((ImageView) itemView.findViewById(R.id.appImage)).setImageDrawable(null);
//          itemView.setLongClickable(false);
//          itemView.setClickable(false);
//          itemView.setVisibility(GONE);
          itemView.setAlpha(0);
        }
      }
    }
    changeItemStatus();
  }

  public void updateAppNameLines(){
    for (int i = 0; i < ROW_NUM*COL_NUM; i++) {
      TextView tvAppName = getChildAt(i).findViewById(R.id.appName);
      tvAppName.setMinLines(Config.appNameLines==2?Config.appNameLines:0);
      tvAppName.setMaxLines(Config.appNameLines);
    }
  }

  public void setAppList(List<ResolveInfo> appList) {
    dataList.clear();
    dataList.addAll(appList);
    refreshIconData();
  }


  private int getItemHeight() {
//        return (getAdjustedHeight() - (ROW_NUM - 2) * dividerSize) / ROW_NUM;
    return getAdjustedHeight() / ROW_NUM;
  }


  private int getItemWidth() {
//        return (getAdjustedWidth() - (COL_NUM - 2) * dividerSize) / COL_NUM;
    return getAdjustedWidth() / COL_NUM;
  }

  public void setColNum(int colNum) {
    this.COL_NUM = colNum;
    resetIconLayout();
  }

  public void setRowNum(int rowNum) {
    this.ROW_NUM = rowNum;
    resetIconLayout();
  }

  private int getAdjustedHeight() {
    return getAdjustedHeight(this);
  }

  private static int getAdjustedHeight(View v) {
    return v.getHeight() - v.getPaddingBottom() - v.getPaddingTop();
  }

  private int getAdjustedWidth() {
    return getAdjustedWidth(this);
  }

  private static int getAdjustedWidth(View v) {
    return v.getWidth() - v.getPaddingLeft() - v.getPaddingRight();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int itemWidthMeasureSpec =makeMeasureSpec(getItemWidth(),EXACTLY);
    int itemHeightMeasureSpec = makeMeasureSpec(getItemHeight(),EXACTLY);
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).measure(itemWidthMeasureSpec,itemHeightMeasureSpec );
    }
  }

  private class ItemClickListener implements OnClickListener {
    int position = 0;

    public ItemClickListener(int position) {
      this.position = position;
    }

    @Override
    public void onClick(View v) {
      if (position>=dataList.size()){
        return;
      }
      if (isDelete) {
        Intent deleteIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + dataList.get(position).activityInfo.packageName));
        v.getContext().startActivity(deleteIntent);
        return;
      }
      ResolveInfo info = dataList.get(position);
      if (info.activityInfo.packageName == AppDataCenter.oneKeyLockPackageName){
        ((Launcher) v.getContext()).lockScreen();
      }else if (info.activityInfo.packageName == AppDataCenter.wifiPackageName) {
        Activity activity = (Activity) getContext();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE)!=PackageManager.PERMISSION_GRANTED) {
//          activity.requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 0);
//        }
        WifiControl.onClickWifiItem();
      }else{
        ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        v.getContext().startActivity(intent);
      }
    }
  }

  private class ItemLongClickListener implements OnLongClickListener {
    int position = 0;

    public ItemLongClickListener(int position) {
      this.position = position;
    }

    @Override
    public boolean onLongClick(View v) {
      if (position>=dataList.size()){
        return false;
      }
      final String packageName = dataList.get(position).activityInfo.packageName;
      if (packageName == AppDataCenter.oneKeyLockPackageName){
        if (!isSystemApp) return true;
        new AlertDialog.Builder(v.getContext())
                .setTitle(R.string.power_title)
                .setItems(R.array.power_menu, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                      Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                      intent.putExtra("android.intent.extra.KEY_CONFIRM", false);//true 确认是否关机
                      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                      getContext().startActivity(intent);
                    } else {
//                                                    Intent intent = new Intent("android.intent.action.REBOOT");
//                                                    intent.putExtra("android.intent.extra.KEY_CONFIRM", false);//true 确认是否关机
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    getContext().startActivity(intent);
                                                    /*intenet.putExtra("nowait",1);
                                                    intenet.putExtra("interval",1);
                                                    intenet.putExtra("window",0);
                                                    getContext().sendBroadcast(intenet);*/
                      PowerManager pManager = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
                      pManager.reboot("重启");
                    }
                  }
                })
                .setPositiveButton("取消", null)
                .show();
      }else if (packageName == AppDataCenter.wifiPackageName) {
        WifiControl.onLongClickWifiItem();
      }else {
        new AlertDialog.Builder(v.getContext())
                .setIcon(dataList.get(position).loadIcon(packageManager))
                .setTitle(dataList.get(position).loadLabel(packageManager))
                .setMessage(getResources().getString(R.string.dialog_pkg_name, packageName))
                .setPositiveButton(R.string.dialog_cancel, null)
                .setNeutralButton(R.string.dialog_hide, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    if (onSingleAppHideChange != null)
                      if (!hideAppPkg.add(packageName))
                        hideAppPkg.remove(packageName);
                    onSingleAppHideChange.change(packageName);
                  }
                })
                .setNegativeButton(R.string.dialog_uninstall, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    Intent deleteIntent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
                    getContext().startActivity(deleteIntent);
                  }
                })
                .show();
      }
      return true;
    }
  }

  private class ItemHideClickListener implements OnClickListener {
    int position = 0;

    public ItemHideClickListener(int position) {
      this.position = position;
    }

    @Override
    public void onClick(View v) {
      String pkg = dataList.get(position).activityInfo.packageName;
      if (hideAppPkg.contains(pkg)) {
        v.setSelected(false);
        hideAppPkg.remove(pkg);
      } else {
        v.setSelected(true);
        hideAppPkg.add(pkg);
      }
    }
  }


  private Point touchDown = null;


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        touchDown = new Point((int) event.getX(), (int) event.getY());
        break;
      case MotionEvent.ACTION_UP:
        if ((event.getX() > touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() > touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toLast();
//                    dataCenter.showLastPage();
          return true;
        }
        if ((event.getX() < touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() < touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toNext();
          return true;
        }
        return false;
//                break;
    }
    return super.onTouchEvent(event);
//        return gestureDetector.onTouchEvent(event);
  }

  //region Description
 /*   SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;//super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            Log.e("onScroll", Math.abs(e1.getX() - e2.getX()) + "   " + distanceX + "    " + distanceY);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.e("onFling", (e1.getX() - e2.getX()) + "    " + (e1.getY() - e2.getY()) + "   " + velocityX + "    " + velocityY + "    " + dragDistance);
           *//* if (((e2.getX() - e1.getX() > dragDistance || e2.getY() - e1.getY() > dragDistance))
                    ||
                    (velocityX > 500 || velocityY > 500)) {
                if (touchListener != null)
                    touchListener.toNext();
                return true;
            } else if ((e1.getX() - e2.getX() < -dragDistance || e1.getY() - e2.getY() < -dragDistance) ||
                    (velocityX < -500 || velocityY < -500)) {
                if (touchListener != null)
                    touchListener.toLast();
                return true;
            }*//*
            if (e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 50) {
                if (touchListener != null)
                    touchListener.toNext();
                return true;
            } else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 50) {
                if (touchListener != null)
                    touchListener.toLast();
                return true;
            }
            return false;//super.onFling(e1, e2, velocityX, velocityY);
        }
    };
*/
  //endregion
  //region Description

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        touchDown = new Point((int) event.getX(), (int) event.getY());
        break;
      case MotionEvent.ACTION_UP:
        if ((event.getX() > touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() > touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toLast();
//                    dataCenter.showLastPage();
          return true;
        }
        if ((event.getX() < touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() < touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toNext();
          return true;
        }
        break;
    }
    return super.onInterceptTouchEvent(event);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        touchDown = new Point((int) event.getX(), (int) event.getY());
        break;
      case MotionEvent.ACTION_UP:
        if ((event.getX() > touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() > touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toLast();
          return true;
        }
        if ((event.getX() < touchDown.x && dragDistance < Math.abs(event.getX() - touchDown.x)) ||
                (event.getY() < touchDown.y && dragDistance < Math.abs(event.getY() - touchDown.y))) {
          if (touchListener != null)
            touchListener.toNext();
          return true;
        }
        break;
    }
    return super.dispatchTouchEvent(event);
  }

  GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener(){
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
      Log.e("====================","onSingleTapConfirmed");
      return super.onSingleTapConfirmed(e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
      Log.e("====================","onLongPress");
      super.onLongPress(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      Log.e("====================","onFling");
      return super.onFling(e1, e2, velocityX, velocityY);
    }
  };

  public void changeItemStatus(){
      for (int i = 0; i < getChildCount(); i++) {
          if (dataList.size()>i){
            if (!isDelete){
              ((ViewGroup) getChildAt(i)).getChildAt(1).setVisibility(GONE);
            }else {
              ((ViewGroup) getChildAt(i)).getChildAt(1).setVisibility(VISIBLE);
              boolean showDelete = false;
              String packageName = dataList.get(i).activityInfo.packageName;
              if (packageName != AppDataCenter.wifiPackageName || packageName != AppDataCenter.oneKeyLockPackageName){
                try {
                  showDelete = (packageManager.getPackageInfo(packageName, 0).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0;
                } catch (PackageManager.NameNotFoundException e) {
                  e.printStackTrace();
                }
              }
              ((ViewGroup) getChildAt(i)).getChildAt(1).setVisibility(VISIBLE);
              getChildAt(i).findViewById(R.id.menu_delete).setVisibility(showDelete ? VISIBLE : GONE);
              getChildAt(i).findViewById(R.id.menu_hide).setSelected(hideAppPkg.contains(dataList.get(i).activityInfo.packageName));
            }
          }
      }
  }

  public void setDelete(boolean delete) {
    isDelete = delete;
    changeItemStatus();
  }

  public boolean isDelete() {
    return isDelete;
  }

  public void setFontSize(float fontSize) {
    this.fontSize = fontSize;
    observable.set(fontSize);
  }

  public float getFontSize() {
    return fontSize;
  }
  //endregion


  public void setSystemApp(boolean systemApp) {
    isSystemApp = systemApp;
  }

  public boolean isSystemApp() {
    return isSystemApp;
  }

  public void setOnSingleAppHideChangeListener(OnSingleAppHideChange onSingleAppHideChange) {
    this.onSingleAppHideChange = onSingleAppHideChange;
  }

  public interface TouchListener {
    void toNext();

    void toLast();
  }

  public interface OnSingleAppHideChange {
    void change(String pkg);
  }
}
