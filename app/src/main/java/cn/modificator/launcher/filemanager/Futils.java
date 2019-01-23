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

import cn.modificator.launcher.R;
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
      Toast.makeText(m, R.string.no_apps_to_open, Toast.LENGTH_LONG).show();  // "Unable to find an app that can open this file"
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
        startintent = Intent.createChooser(intent, c.getText(R.string.choose_app_title));
      else startintent = intent;
      try {
        c.startActivity(startintent);
      } catch (ActivityNotFoundException e) {
        e.printStackTrace();
        Toast.makeText(c, R.string.no_apps_to_open, Toast.LENGTH_SHORT).show();
        openWith(f, c);
      }
    } else {
      openWith(f, c);
    }

  }


  public static void openWith(final DocumentFile f, final Context c) {

    AlertDialog.Builder a = new AlertDialog.Builder(c);
    a.setTitle(R.string.choose_app_title);

    String[] items = c.getResources().getStringArray(R.array.file_types);

    a.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        String[] items_mime = c.getResources().getStringArray(R.array.file_types_mime);

        switch (which) {
          case 0:
            intent.setDataAndType(f.getUri(), items_mime[0]);
            break;
          case 1:
            intent.setDataAndType(f.getUri(), items_mime[1]);
            break;
          case 2:
            intent.setDataAndType(f.getUri(), items_mime[2]);
            break;
          case 3:
            intent.setDataAndType(f.getUri(), items_mime[3]);
            break;
          case 4:
            intent.setDataAndType(f.getUri(), items_mime[4]);
            break;
        }
        try {
          c.startActivity(intent);
        } catch (Exception e) {
          Toast.makeText(c, R.string.no_apps_to_open, Toast.LENGTH_SHORT).show();
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
