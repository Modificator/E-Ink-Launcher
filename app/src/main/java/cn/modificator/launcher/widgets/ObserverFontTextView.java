package cn.modificator.launcher.widgets;

import android.content.Context;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Modificator
 * time: 16/12/3.上午2:03
 * des:create file and achieve model
 */

public class ObserverFontTextView extends TextView implements Observer {
  public ObserverFontTextView(Context context) {
    super(context);
  }

  public ObserverFontTextView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public ObserverFontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void update(Observable o, Object arg) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, (Float) arg);
//    requestLayout();
  }
}
