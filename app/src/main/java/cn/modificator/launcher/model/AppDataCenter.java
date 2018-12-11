package cn.modificator.launcher.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.modificator.launcher.ComparableResolveInfo;
import cn.modificator.launcher.Config;
import cn.modificator.launcher.SortActivity;
import cn.modificator.launcher.widgets.EInkLauncherView;

/**
 * Created by mod on 16-4-22.
 */
public class AppDataCenter {
    Context mContext;
    private static List<ComparableResolveInfo> mApps;
    private static List<ComparableResolveInfo> AppListWithoutHidenApps;
    private static List<ComparableResolveInfo> AllApps;
    int pageIndex = 0;
    int pageCount = 1;
    //列数
    int colNum = 5;
    //行数
    int rowNum = 5;
    EInkLauncherView launcherView;
    TextView pageStatus;
    private Set<String> hideApps = new HashSet<>();

    public AppDataCenter (Context context) {
        this.mContext = context;
        mApps = new ArrayList<>();
        AllApps = new ArrayList<>();

        AppListWithoutHidenApps = new ArrayList<>();
        mApps = AppListWithoutHidenApps;
//        loadApps();
        instance = this;
    }

    public static List<ComparableResolveInfo> getAppListWithoutHidenApps () {
        return AppListWithoutHidenApps;
    }

    public static void getAppListFromPre () {
        for (ComparableResolveInfo comparableResolveInfo : AppListWithoutHidenApps) {
            comparableResolveInfo.setPosition(Config.instance.getAppListDate(comparableResolveInfo.getResolveInfo().activityInfo.packageName));
        }
    }

    public static void setAppsListToPre () {
        for (ComparableResolveInfo comparableResolveInfo : AppListWithoutHidenApps) {
            Config.instance.setAppListDate(comparableResolveInfo.getResolveInfo().activityInfo.packageName, comparableResolveInfo.getPosition());
        }
    }


    public static AppDataCenter instance = null;


    public void setLauncherView (EInkLauncherView launcherView) {
        this.launcherView = launcherView;
        this.launcherView.setOnSingleAppHideChangeListener(new EInkLauncherView.OnSingleAppHideChange() {
            @Override
            public void change (String pkg) {
//                if (!hideApps.add(pkg))
//                    hideApps.remove(pkg);
                CompareMappsToHideAppsAndRemoveHideApps();
                refreshAppList();
            }
        });
        launcherView.setHideAppPkg(hideApps);
        setPageShow();
    }

    public void setPageStatus (TextView pageStatus) {
        this.pageStatus = pageStatus;
        pageStatus.setText((pageIndex + 1) + "/" + (pageCount + 1));
    }

    public void setHideApps (Set<String> hideApps) {
        this.hideApps.clear();
        this.hideApps.addAll(hideApps);
        CompareMappsToHideAppsAndRemoveHideApps();
    }

    public Set<String> getHideApps () {
        return hideApps;
    }


    public void CompareMappsToHideAppsAndRemoveHideApps () {
        // iterator 遍历 AppListWithoutHidenApps 删除 hide apps 已有元素  而不是直接清空 AppListWithoutHidenApps 然后再添加
        if (launcherView != null) {
//        if (launcherView!=null&&!launcherView.getHideAppPkg().isEmpty()) {
            hideApps.clear();
            hideApps.addAll(launcherView.getHideAppPkg());
        }
        AppListWithoutHidenApps.clear();
        AppListWithoutHidenApps.addAll(AllApps);
        Iterator<ComparableResolveInfo> iterator = AppListWithoutHidenApps.iterator();
        while (iterator.hasNext()) {
            ComparableResolveInfo comparableResolveInfo = iterator.next();
            if (hideApps.contains(comparableResolveInfo.getResolveInfo().activityInfo.packageName)) {
                iterator.remove();
            }
//      if ("cn.modificator.launcher.Launcher".equals(resolveInfo.activityInfo.name)){
//        iterator.remove();
//      }
        }
        Collections.sort(AppListWithoutHidenApps);
        mApps = AppListWithoutHidenApps;
        updatePageCount();
    }
//  private void loadApps() {
//    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//    if (launcherView != null) {
////        if (launcherView!=null&&!launcherView.getHideAppPkg().isEmpty()) {
//      hideApps.clear();
//      hideApps.addAll(launcherView.getHideAppPkg());
//    }
//    mApps.clear();
//    for (ResolveInfo resolveInfo : mContext.getPackageManager().queryIntentActivities(mainIntent, 0)) {
//      if ("cn.modificator.launcher.Launcher".equals(resolveInfo.activityInfo.name)) continue;
//      if (!hideApps.contains(resolveInfo.activityInfo.packageName)) {
//        mApps.add(resolveInfo);
//      }
//    }
////        mApps.addAll();
//    updatePageCount();
//  }

    private void loadAllApps () {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        AllApps.clear();
        for (int i = 0; i < mContext.getPackageManager().queryIntentActivities(mainIntent, 0).size(); i++) {
            AllApps.add(new ComparableResolveInfo(mContext.getPackageManager().
                    queryIntentActivities(mainIntent, 0).get(i), i));
        }
        AppListWithoutHidenApps.clear();
        AppListWithoutHidenApps.addAll(AllApps);
//    launcherView.setHideAppPkg(hideApps);
        updatePageCount();
    }

    public void showNextPage () {
        if (pageIndex >= pageCount) return;
        pageIndex++;
        setPageShow();
    }

    public void showLastPage () {
        if (pageIndex <= 0) return;
        pageIndex--;
        setPageShow();
    }

    public void setColNum (int colNum) {
        this.colNum = colNum;
        updatePageCount();
        setPageShow();
    }

    public void setRowNum (int rowNum) {
        this.rowNum = rowNum;
        updatePageCount();
        setPageShow();
    }

    public void refreshAppList () {
        refreshAppList(false);
    }

    public void refreshAppList (boolean showAll) {
        if (showAll) {
            mApps = AllApps;
        } else {
            mApps = AppListWithoutHidenApps;
        }
        setPageShow();
    }

    public void LoadAllApps () {
        loadAllApps();
    }

    public void SortAppList () {
        //排序 AppListWithoutHidenApps
        Collections.sort(AppListWithoutHidenApps);
        mApps = AppListWithoutHidenApps;
    }

    private void setPageShow () {
        int itemCount = colNum * rowNum;
        int pageStart = pageIndex * itemCount;
        int pageEnd = (pageStart + itemCount) > mApps.size() ? mApps.size() : (pageStart + itemCount);
        launcherView.setAppList(mApps.subList(pageStart, pageEnd));
        pageStatus.setText((pageIndex + 1) + "/" + (pageCount + 1));
    }

    private void updatePageCount () {
//        pageCount = (int) Math.ceil(mApps.size() * 1f / (colNum * rowNum));
        pageCount = mApps.size() / (colNum * rowNum) - (mApps.size() % (colNum * rowNum) == 0 ? 1 : 0);
        pageCount = pageCount < 0 ? 0 : pageCount;
        pageIndex = pageIndex > pageCount ? pageCount : pageIndex;
    }

   /* public boolean isHide(String pkg) {
        return hideApps.contains(pkg);
    }

    public void addHide(String pkg) {
        hideApps.add(pkg);
    }

    public void removeHide(String pkg) {
        hideApps.remove(pkg);
    }*/
}
