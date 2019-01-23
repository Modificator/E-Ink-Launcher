package cn.modificator.launcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import androidx.annotation.Nullable;

public class CrashDetailPage extends Activity {

  private TextView btnReLaunch, tvContent;
  private Context mContext;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    initViews();
  }

  private void initViews() {
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setBackgroundColor(Color.WHITE);

    ScrollView scrollView = new ScrollView(this);
    tvContent = new TextView(this);
    tvContent.setTextColor(Color.BLACK);
    tvContent.setTextSize(12);

    scrollView.addView(tvContent);


    root.addView(scrollView, new LinearLayout.LayoutParams(-1, -1, 1));

    btnReLaunch = new TextView(this);
    btnReLaunch.setText(R.string.restart_launcher);
    btnReLaunch.setTextColor(Color.BLACK);
    btnReLaunch.setGravity(Gravity.CENTER);

    View divider = new View(this);
    divider.setBackgroundColor(Color.BLACK);
    root.addView(divider, new ViewGroup.LayoutParams(-1, 1));
    root.addView(btnReLaunch, new LinearLayout.LayoutParams(-1, Utils.dp2Px(this, 40)));

    setContentView(root);
  }

  private void fillErrorContent() {
    btnReLaunch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, Launcher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
      }
    });

    tvContent.setText("");
    String title = getText(R.string.report_error_title).toString();
    SpannableString titleSpan = new SpannableString(title);
    titleSpan.setSpan(new AbsoluteSizeSpan(25, true), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvContent.append(titleSpan);
    tvContent.append(getText(R.string.report_error_description));


    if (getIntent().hasExtra("crashFile")) {
      String fileName = getIntent().getStringExtra("crashFile");
      File crashFile = new File(getExternalFilesDir("crash"), fileName);
      try {
        char[] readData = new char[(int) crashFile.length()];
        FileReader reader = new FileReader(crashFile);
        reader.read(readData);
        tvContent.append(new String(readData));
        reader.close();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    fillErrorContent();

  }
}
