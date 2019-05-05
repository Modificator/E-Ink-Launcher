package cn.modificator.launcher.settings.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TmpLocalVIew extends View {
    float x, y;

    public TmpLocalVIew(Context context) {
        super(context);
    }

    public TmpLocalVIew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TmpLocalVIew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, y, getWidth(), y, paint);
        canvas.drawLine(x, 0, x, getHeight(), paint);
    }

    @Override
    public void setX(float x) {
        this.x = x;
        invalidate();
    }

    @Override
    public void setY(float y) {
        this.y = y;
        invalidate();
    }

    public void setPoint(float x,float y){
        this.x = x;
        this.y = y;
        invalidate();
    }
}

