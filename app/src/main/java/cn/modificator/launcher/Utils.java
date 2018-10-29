package cn.modificator.launcher;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import androidx.core.graphics.drawable.DrawableCompat;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import cn.modificator.launcher.filemanager.FileComparator;

/**
 * Created by mod on 16-5-6.
 */
public class Utils {

  public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
    final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
    DrawableCompat.setTintList(wrappedDrawable, colors);
    return wrappedDrawable;
  }

  public static List<File> getFileListByDirPath(File path) {
    File[] files = path.listFiles();

    if (files == null) {
      return new ArrayList<>();
    }

    List<File> result = Arrays.asList(files);
    Collections.sort(result, new FileComparator());
    return result;
  }

  public static String getReadableFileSize(long size) {
//        if (size <= 0) return "0";
//        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
//        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
//        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    DecimalFormat formater = new DecimalFormat("####.00");
    if (size < 1024 * 0.8) {
      return size + "bytes";
    } else if (size < 1024 * 1024 * 0.8) {
      float kbsize = size / 1024f;
      return formater.format(kbsize) + "KB";
    } else if (size < 1024 * 1024 * 1024 * 0.8) {
      float mbsize = size / 1024f / 1024f;
      return formater.format(mbsize) + "MB";
    } else if (size < 1024 * 1024 * 1024 * 1024 * 0.8) {
      float gbsize = size / 1024f / 1024f / 1024f;
      return formater.format(gbsize) + "GB";
    } else {
      float tbsize = size / 1024f / 1024f / 1024f / 1024f;
      return formater.format(tbsize) + "TB";
    }
  }

  public static int dp2Px(Context context, float dp) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  public static String getAMPMCNString(int hours, int ampm) {
    if (ampm == Calendar.AM) {
      if (hours < 5) {
        return sAmPmCN[0];
      } else if (hours >= 5 && hours < 7) {
        return sAmPmCN[1];
      } else if (hours >= 7 && hours < 9) {
        return sAmPmCN[2];
      } else if (hours >= 9 && hours < 12) {
        return sAmPmCN[3];
      } else {
        return sAmPmCN[0];
      }
    } else {
      if (hours == 0) {
        return sAmPmCN[4];
      } else if (hours < 6) {
        return sAmPmCN[5];
      } else if (hours >= 6 && hours <= 9) {
        return sAmPmCN[6];
      } else if (hours > 9 && hours < 12) {
        return sAmPmCN[7];
      } else if (hours == 12) {
        return sAmPmCN[4];
      } else {
        return sAmPmCN[4];
      }
    }
  }

  private static final String[] sAmPmCN = new String[]{
      "凌晨", "黎明", "早晨", "上午", "中午", "下午", "晚上", "深夜"
  };

}
