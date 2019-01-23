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
    appName.setText(R.string.app_name);
    appName.setTextSize(30);
    root.addView(appName);
    View line = new View(context);
    line.setBackgroundColor(0xff000000);
    root.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(context, 1)));

    TextView authorInfo = new TextView(context);

    authorInfo.setText(R.string.author_info);
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
    thanksView.setText(R.string.thanks);
    thanksView.setTextSize(15);
    thanksView.setPadding(0, Utils.dp2Px(context, 10), 0, Utils.dp2Px(context, 10));
    root.addView(thanksView);

    line = new View(context);
    line.setBackgroundColor(0xff000000);
    root.addView(line, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2Px(context, 1)));

    TextView launcherInfo = new TextView(context);
    launcherInfo.setText(R.string.launcher_info);
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
