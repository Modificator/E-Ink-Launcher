package cn.modificator.launcher.filemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.modificator.launcher.R;

/**
 * Created by Modificator
 * time: 16/8/6.下午11:59
 * des:create file and achieve model
 */

public class FileAdapter extends BaseAdapter {
  List<File> dataList;

  public FileAdapter(List<File> dataList) {
    this.dataList = dataList;
  }

  @Override
  public int getCount() {
    return dataList.size();
  }

  @Override
  public Object getItem(int position) {
    return dataList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
      holder = new ViewHolder(convertView);
      convertView.setTag(holder);
    } else
      holder = (ViewHolder) convertView.getTag();
    File currentFile = dataList.get(position);

    FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
    holder.mFileImage.setImageResource(fileType.getIcon());
    holder.mFileSubtitle.setText(fileType.getDescription());
    holder.mFileTite.setText(currentFile.getName());
    return convertView;
  }

  public class ViewHolder {
    private ImageView mFileImage;
    private TextView mFileTite;
    private TextView mFileSubtitle;

    public ViewHolder(View itemView) {
      mFileImage = (ImageView) itemView.findViewById(R.id.item_file_image);
      mFileTite = (TextView) itemView.findViewById(R.id.item_file_title);
      mFileSubtitle = (TextView) itemView.findViewById(R.id.item_file_subtitle);
    }
  }
}
