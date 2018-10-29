package cn.modificator.launcher.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.modificator.launcher.R;
import cn.modificator.launcher.Utils;

/**
 * Created by Modificator
 * time: 16/8/6.下午11:59
 * des:create file and achieve model
 */
public class FileManager extends Activity {

  Stack<File> fileLog = new Stack<>();
  TextView tvFilePath;
  List<File> currFileList = new ArrayList<>();
  File currFile;
  FileAdapter adapter = new FileAdapter(currFileList);
  ListView mList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_file_manager);

    initViews();
  }

  private void initViews() {
    tvFilePath = (TextView) findViewById(R.id.tvFilePath);
    mList = (ListView) findViewById(R.id.mFileList);
    ((ImageView) findViewById(R.id.toSetting)).setImageDrawable(Utils.tintDrawable(getResources().getDrawable(R.drawable.navibar_icon_settings_highlight), ColorStateList.valueOf(0xff000000)));
    mList.setAdapter(adapter);

    tvFilePath.setMovementMethod(ScrollingMovementMethod.getInstance());

    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = currFileList.get(position);
        if (file.isFile()) {
          Futils.openFile(file, FileManager.this);
//                    try {
//                        Intent intent = new Intent();
//                        intent.setData(Uri.fromFile(file));
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(FileManager.this, "没有应用可以打开这个文件", 0).show();
//                    }
          return;
        }
        if (currFile != null) {
          fileLog.add(currFile);
        }
        loadFileList(file);
      }
    });
    mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(FileManager.this)
            .setItems(new String[]{getResources().getString(R.string.delete)}, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                File file = currFileList.get(position);
                deleteFile(file);
                currFileList.remove(file);
                adapter.notifyDataSetInvalidated();
              }
            }).setPositiveButton(R.string.dialog_cancel, null).show();
        return true;
      }
    });

    findViewById(R.id.btnLastPath).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadFileList(fileLog.pop());
      }
    });
    findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.toSetting).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final EditText text = new EditText(FileManager.this);
        text.setSingleLine();
        text.setText(getSharedPreferences("launcherPropertyFile", MODE_PRIVATE).getString("fileRoot", "/sdcard"));
        LinearLayout linearLayout = new LinearLayout(FileManager.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView msg = new TextView(FileManager.this);
        msg.setText(R.string.default_path_setting);
        msg.setTextSize(17);
        text.setTextSize(18);
        linearLayout.addView(msg);
        linearLayout.addView(text);
        new AlertDialog.Builder(FileManager.this)
            .setView(linearLayout)
            .setPositiveButton(R.string.dialog_confim, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                String rootFile = text.getText().toString();
                fileLog.clear();
                loadFileList(new File(rootFile));
                getSharedPreferences("launcherPropertyFile", MODE_PRIVATE).edit().putString("fileRoot", rootFile).commit();
              }
            }).setNegativeButton(R.string.dialog_cancel, null).show();

      }
    });

//        fileLog.add(new File("/sdcard"));

    loadFileList(new File(getSharedPreferences("launcherPropertyFile", MODE_PRIVATE).getString("fileRoot", "/sdcard")));
  }

  private void loadFileList(File file) {
    currFileList.clear();
    currFileList.addAll(Utils.getFileListByDirPath(file));
    adapter.notifyDataSetInvalidated();
    tvFilePath.setText(file.getAbsolutePath());
    currFile = file;
    findViewById(R.id.btnLastPath).setVisibility(fileLog.isEmpty() ? View.INVISIBLE : View.VISIBLE);
  }

  @Override
  public void onBackPressed() {
    if (fileLog.isEmpty())
      super.onBackPressed();
    else
      loadFileList(fileLog.pop());
  }

  /*
      @Override
      public boolean onKeyUp(int keyCode, KeyEvent event) {
          switch (keyCode){
              case KeyEvent.KEYCODE_PAGE_DOWN:
                  mList.scrollBy(0,Utils.dp2Px(this,80)*(mList.getLastVisiblePosition()-mList.getFirstVisiblePosition()));
                  return true;
              case KeyEvent.KEYCODE_PAGE_UP:
                  mList.scrollBy(0,-Utils.dp2Px(this,80));
          }
          return true;//super.onKeyUp(keyCode, event);
      }*/
  private void deleteFile(File file) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        deleteFile(f);
      }
    }
    file.delete();
  }
}
