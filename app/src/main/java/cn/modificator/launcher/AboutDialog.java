package cn.modificator.launcher;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Modificator
 * time: 17/1/7.下午2:40
 * des:create file and achieve model
 */

public class AboutDialog {

  Context context;

  private AboutDialog(Context context) {
    this.context = context;
  }

  public static AboutDialog getInstance(Context context) {
    return new AboutDialog(context);
  }

  private View initLayout() {
    LinearLayout root = new LinearLayout(context);
    int padding = Utils.dp2Px(context, 15);
    root.setPadding(padding, padding, padding, padding);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setBackgroundColor(0xffffffff);
    TextView appName = new TextView(context);
    appName.setText("E-Ink Launcher");
    appName.setTextSize(30);
    root.addView(appName);
    View line = new View(context);
    line.setBackgroundColor(0xff000000);
    root.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(context, 1)));

    TextView authorInfo = new TextView(context);

    authorInfo.setText(" Author:Modificator\n Email:yunshangcn@gmail.com\n Weibo: weibo.com/yunshangcn");
    authorInfo.setTextSize(18);
    authorInfo.setTextColor(0xff000000);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      authorInfo.setLineSpacing(authorInfo.getLineSpacingExtra(), 1.2f);
    }
    LinearLayout.LayoutParams authorInfoLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    authorInfoLP.topMargin = Utils.dp2Px(context, 10);
    authorInfoLP.bottomMargin = Utils.dp2Px(context, 10);
    root.addView(authorInfo, authorInfoLP);

    line = new View(context);
    line.setBackgroundColor(0xff000000);
    root.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(context, 1)));

    TextView thanksView = new TextView(context);
    thanksView.setText("Thanks:\nMaciej Haudek <kontakt@haudek.com>\n");  
    thanksView.setTextSize(15);
    thanksView.setPadding(0, Utils.dp2Px(context, 10), 0, Utils.dp2Px(context, 10));
    root.addView(thanksView);

    line = new View(context);
    line.setBackgroundColor(0xff000000);
    root.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(context, 1)));

    TextView launcherInfo = new TextView(context);
    launcherInfo.setText("E-Ink Launcher 是作者君闲暇之作，JDR没桌面，又找不到\n自己满意的，果断自己动手写了一个。\n主要功能：\n1.当然是显示App了\n2.App展示密度调整\n3.App名称字体大小调整\n4.卸载/隐藏App\n5.一键锁屏\n6.一键开关WIFI\n7.可以换图标了，长按图标会出现包名，把图标重命名成包名+文件后缀，然后连接电脑放到E-Ink Launcher/icon 下就可以了\nPS：图标长按有惊喜");
    launcherInfo.setTextColor(0xff000000);
    launcherInfo.setTextSize(14);
    root.addView(launcherInfo);

    return root;
  }

  public void show() {
    new AlertDialog.Builder(context)
        .setView(initLayout())
        .setPositiveButton(R.string.dialog_close, null)
        .show();
  }
}
