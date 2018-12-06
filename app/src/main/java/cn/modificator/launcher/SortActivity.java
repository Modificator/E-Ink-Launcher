package cn.modificator.launcher;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import cn.modificator.launcher.model.AppDataCenter;

public class SortActivity extends Activity {

    private ListView listView;
    private List<ComparableResolveInfo> appList;
    private TextView Save_btn;
    PackageManager packageManager;
    SortItemAdapter sortItemAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        appList = AppDataCenter.getAppListWithoutHidenApps();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);
        packageManager = getPackageManager();
        listView = findViewById(R.id.sort_list);
        sortItemAdapter = new SortItemAdapter(getApplicationContext(),appList,packageManager);
        listView.setAdapter(sortItemAdapter);
        sortItemAdapter.SetSortItemBtnClickListener(new SortItemAdapter.OnSortItemUpListener() {
                                                        @Override
                                                        public void onUpClick (int i) {
                                                            ItemMoveUp(i);
                                                        }
                                                    }, new SortItemAdapter.OnSortItemDownListener() {
                                                        @Override
                                                        public void onDownClick (int i) {
                                                            ItemMoveDown(i);
                                                        }
                                                    }
        );
        Save_btn = findViewById(R.id.sort_save_btn);
        Save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                AppDataCenter.setAppsListToPre();
                finish();
            }
        });
    }
    private void ItemMoveUp(int position){
        if (position>0){
            appList.get(position).setPosition(appList.get(position).getPosition()-1);
            Collections.swap(appList,position-1,position);
            sortItemAdapter.notifyDataSetChanged();
            AppDataCenter.instance.refreshAppList();
        }
    }
    private void ItemMoveDown(int position){
        if (position<appList.size()-1){
            appList.get(position).setPosition(appList.get(position).getPosition()+1);
            Collections.swap(appList,position,position+1);
            sortItemAdapter.notifyDataSetChanged();
            AppDataCenter.instance.refreshAppList();
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
