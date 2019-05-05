package cn.modificator.launcher.settings.ui;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import cn.modificator.launcher.R;

public class SubPreference extends Preference implements View.OnLongClickListener {

    private int mContent;
    private int mLongClickContent;
    private boolean mLongClick;

    public SubPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SubPreference);
        for (int i = a.getIndexCount() - 1; i >= 0; i--) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SubPreference_content) {
                mContent = a.getResourceId(attr, 0);
            } else if (attr == R.styleable.SubPreference_longClickContent) {
                mLongClickContent = a.getResourceId(attr, 0);
            }
        }
        a.recycle();
        setFragment("");
    }

    @Override
    public String getFragment() {
        return super.getFragment();
    }

    public int getContent() {
        return mLongClick ? mLongClickContent : mContent;
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnLongClickListener(this);
    }

    @Override
    protected void onClick() {
        mLongClick = false;
        super.onClick();
    }

    @Override
    public boolean onLongClick(View view) {
        if (mLongClickContent != 0) {
            mLongClick = true;
            // This is unfortunately the only way to simulate a click on Support Preferences
            getPreferenceManager().getOnPreferenceTreeClickListener().onPreferenceTreeClick(this);
            return true;
        } else {
            return false;
        }
    }
}