package cn.modificator.launcher.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import cn.modificator.launcher.R;

/**
 * Created by mod on 15-10-30.
 */
public class RatioImageView extends ImageView {

  /**
   * 以哪边为参考，默认为宽
   */
  ReferenceType reference = ReferenceType.WIDTH;
  /**
   * 宽的比例
   */
  double ratioWidth = 1;
  /**
   * 高的比例
   */
  double ratioHeight = 1;

  public enum ReferenceType {
    WIDTH,
    ReferenceType, HEIGHT
  }

  public RatioImageView(Context context) {
    super(context);
  }

  public RatioImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout, 0, 0);
    //获取参考边
    reference = typedArray.getInt(R.styleable.RatioLayout_reference, 0) == 0 ? ReferenceType.WIDTH : ReferenceType.HEIGHT;
    //获取高比例
    ratioHeight = typedArray.getFloat(R.styleable.RatioLayout_ratioHeight, 1);
    //获取宽比例
    ratioWidth = typedArray.getFloat(R.styleable.RatioLayout_ratioWidth, 1);
    typedArray.recycle();
  }

  public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout, defStyleAttr, 0);
    //获取参考边
    reference = typedArray.getInt(R.styleable.RatioLayout_reference, 0) == 0 ? ReferenceType.WIDTH : ReferenceType.HEIGHT;
    //获取高比例
    ratioHeight = typedArray.getFloat(R.styleable.RatioLayout_ratioHeight, 1);
    //获取宽比例
    ratioWidth = typedArray.getFloat(R.styleable.RatioLayout_ratioWidth, 1);
    typedArray.recycle();
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    /**
     * 如果以宽慰基准边则宽不变，高按比例得出具体数值，反之亦然
     */
    setMeasuredDimension(View.getDefaultSize(0, reference == ReferenceType.WIDTH ? widthMeasureSpec :
            (int) (heightMeasureSpec / ratioHeight * ratioWidth)),
        View.getDefaultSize(0, reference == ReferenceType.HEIGHT ? heightMeasureSpec :
            (int) (widthMeasureSpec / ratioWidth * ratioHeight)));

    int childSpec = reference == ReferenceType.WIDTH ? getMeasuredWidth() : getMeasuredHeight();
    /**
     * 获取非基准边的尺寸
     */
    int measureSpec = reference == ReferenceType.HEIGHT ? View.MeasureSpec.makeMeasureSpec(
        (int) (childSpec / ratioHeight * ratioWidth), View.MeasureSpec.EXACTLY) :
        View.MeasureSpec.makeMeasureSpec(
            (int) (childSpec / ratioWidth * ratioHeight), View.MeasureSpec.EXACTLY);

    super.onMeasure(reference == ReferenceType.WIDTH ? widthMeasureSpec : measureSpec, reference == ReferenceType.HEIGHT ? heightMeasureSpec : measureSpec);
  }
}
