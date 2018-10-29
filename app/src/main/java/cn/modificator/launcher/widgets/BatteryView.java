package cn.modificator.launcher.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by mod on 16-4-25.
 */

public class BatteryView extends View {
  Paint circlePaint;
  TextPaint textPaint;
  int maxProgress = 100;
  int progress = 0;

  public BatteryView(Context context) {
    super(context);
    init();
  }

  public BatteryView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    circlePaint = new Paint();
    circlePaint.setStyle(Paint.Style.STROKE);
    circlePaint.setAntiAlias(true);
    textPaint = new TextPaint();
    textPaint.setColor(0xff000000);
    textPaint.setAntiAlias(true);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int size = getWidth() > getHeight() ? getHeight() : getWidth();
    circlePaint.setStrokeWidth(size / 10);
    circlePaint.setColor(0xffcccccc);
    canvas.drawCircle(getWidth() / 2, getHeight() / 2, (size - circlePaint.getStrokeWidth()) / 2, circlePaint);
    RectF rectF = new RectF(
        (getWidth() - size + circlePaint.getStrokeWidth()) / 2,
        (getHeight() - size + circlePaint.getStrokeWidth()) / 2,
        (getWidth() - size - circlePaint.getStrokeWidth()) / 2 + size,
        (getHeight() - size - circlePaint.getStrokeWidth()) / 2 + size
    );
    circlePaint.setColor(0xff000000);
    canvas.drawArc(rectF, -90, progress * 1f / maxProgress * 360, false, circlePaint);
    textPaint.setTextSize(size / 2.8f);
    drawText(canvas);
  }

  private void drawText(Canvas canvas) {
    String showText = String.format("%02d", (int) Math.round(progress * 1f / maxProgress * 100));
    Rect rect = new Rect();
    textPaint.getTextBounds(showText, 0, showText.length(), rect);
    textPaint.setFakeBoldText(true);
    canvas.translate(getWidth() / 2f, getHeight() / 2f);
    canvas.drawText(showText,
        -(rect.right - rect.left) / 1.9f,
        (rect.bottom - rect.top) / 2f, textPaint);
  }

  public void setMaxProgress(int maxProgress) {
    this.maxProgress = maxProgress;
    invalidate();
  }

  public void setProgress(int progress) {
    this.progress = progress;
    invalidate();
  }
    /*private void drawChargeIcon(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xff000000);
        int size = getHeight();
        float margin = size * 0.2f;
        Path path = new Path();
        path.moveTo(size / 3f+margin, 0+margin);
        path.lineTo(size / 3f * 2-margin, 0+margin);
        path.lineTo(size * 0.45f, size * 0.45f);
        path.lineTo(size * 0.8f, size * 0.45f);
        path.lineTo(size * 0.25f, size);
        path.lineTo(size * 0.45f, size * 0.55f);
        path.lineTo(size * 0.25f, size * 0.55f);*//*
        path.moveTo(size / 3f, 0);
        path.lineTo(size / 3f * 2, 0);
        path.lineTo(size * 0.45f, size * 0.45f);
        path.lineTo(size * 0.8f, size * 0.45f);
        path.lineTo(size * 0.25f, size);
        path.lineTo(size * 0.45f, size * 0.55f);
        path.lineTo(size * 0.25f, size * 0.55f);*//*
        path.close();
        canvas.drawPath(path, paint);

    }*/

  @Override
  protected void onWindowVisibilityChanged(int visibility) {
    super.onWindowVisibilityChanged(visibility);
    if (visibility == VISIBLE) {

    } else {

    }
  }

}
