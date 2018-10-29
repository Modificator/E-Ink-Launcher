package cn.modificator.launcher.filemanager;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Modificator
 * time: 16/8/6.下午11:59
 * des:create file and achieve model
 */
public class CompositeFilter implements FileFilter, Serializable {

  private ArrayList<FileFilter> mFilters;

  public CompositeFilter(ArrayList<FileFilter> filters) {
    mFilters = filters;
  }

  @Override
  public boolean accept(File f) {
    for (FileFilter filter : mFilters) {
      if (!filter.accept(f)) {
        return false;
      }
    }

    return true;
  }
}
