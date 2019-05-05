package cn.modificator.launcher.settings;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.IdRes;

import static android.view.View.NO_ID;

public class BasePopupWindow extends PopupWindow implements View.OnClickListener {
    protected FrameLayout rootView;

    public BasePopupWindow(Context context) {
        super(context);
        rootView = new FrameLayout(context);
        super.setContentView(rootView);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x0));
        setWidth(-1);
        setHeight(-1);
        setAnimationStyle(0);

        rootView.setOnClickListener(this);
    }

    @Override
    public void setContentView(View contentView) {
        rootView.addView(contentView);
    }

    @Override
    public void onClick(View v) {
        if (v == rootView) {
            dismiss();
        }
    }

    public final <T extends View> T findViewById(@IdRes int id) {
        if (id == NO_ID) {
            return null;
        }
        return rootView.findViewById(id);
    }
}
