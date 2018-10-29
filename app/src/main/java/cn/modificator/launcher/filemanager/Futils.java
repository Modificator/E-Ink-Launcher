package cn.modificator.launcher.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.documentfile.provider.DocumentFile;

import android.widget.Toast;

import java.io.File;

import cn.modificator.launcher.filemanager.MimeTypeUtil;

/**
 * Created by Modificator
 * time: 17/1/31.上午11:06
 * des:create file and achieve model
 */

public class Futils {
  public static void openFile(final File f, final Activity m) {
    //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(m);
    DocumentFile documentFile = DocumentFile.fromFile(f);
    try {
      openunknown(documentFile, m, false);
    } catch (Exception e) {
      Toast.makeText(m, "无法找到可以打开此文件的应用", Toast.LENGTH_LONG).show();
      openWith(documentFile, m);
    }
  }


  public static void openunknown(DocumentFile f, Context c, boolean forcechooser) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    String type = f.getType();
    if (type != null && type.trim().length() != 0 && !type.equals("*/*")) {
      type = MimeTypeUtil.getMIMEType(f.getName());
    }
    if (type != null && type.trim().length() != 0 && !type.equals("*/*")) {
      intent.setDataAndType(f.getUri(), type);
      Intent startintent;
      if (forcechooser)
        startintent = Intent.createChooser(intent, "打开方式");
      else startintent = intent;
      try {
        c.startActivity(startintent);
      } catch (ActivityNotFoundException e) {
        e.printStackTrace();
        Toast.makeText(c, "无法找到可以打开此文件的应用", Toast.LENGTH_SHORT).show();
        openWith(f, c);
      }
    } else {
      openWith(f, c);
    }

  }


  public static void openWith(final DocumentFile f, final Context c) {

    AlertDialog.Builder a = new AlertDialog.Builder(c);
    a.setTitle("打开方式");
    String[] items = new String[]{"文本", "图片", "视频", "音频", "其他"};

    a.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        switch (which) {
          case 0:
            intent.setDataAndType(f.getUri(), "text/*");
            break;
          case 1:
            intent.setDataAndType(f.getUri(), "image/*");
            break;
          case 2:
            intent.setDataAndType(f.getUri(), "video/*");
            break;
          case 3:
            intent.setDataAndType(f.getUri(), "audio/*");
            break;
          case 4:
            intent.setDataAndType(f.getUri(), "*/*");
            break;
        }
        try {
          c.startActivity(intent);
        } catch (Exception e) {
          Toast.makeText(c, "无法找到可以打开此文件的应用", Toast.LENGTH_SHORT).show();
          openWith(f, c);
        }
      }
    });
    try {
      a.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
