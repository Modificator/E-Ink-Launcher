package cn.modificator.launcher.settings;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import cn.modificator.launcher.R;

public class MainSetting extends BasePopupWindow {
    public MainSetting(Context context) {
        super(context);
        setContentView(LayoutInflater.from(context).inflate(R.layout.setting_main,rootView,false));
        initView();
    }

    private void initView(){
        findViewById(R.id.btn_close).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor, 0, anchor.getHeight(), Gravity.BOTTOM | Gravity.RIGHT);

    }
}
