package cn.modificator.launcher.pageindicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import cn.modificator.launcher.DropTarget;
import cn.modificator.launcher.Launcher;
import cn.modificator.launcher.dragndrop.DragController;
import cn.modificator.launcher.dragndrop.DragOptions;

public class PageNumDragTarget extends AppCompatTextView implements DropTarget, DragController.DragListener, View.OnClickListener {

    protected Launcher mLauncher;
    protected boolean mActive;

    private int pageIndex = -1;

    public PageNumDragTarget(Context context) {
        this(context, null);
    }

    public PageNumDragTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageNumDragTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTextColor(Color.BLACK);
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        setOnClickListener(this);
    }

    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    public void onDragStart(DragObject dragObject, DragOptions options) {
        mActive = true;
    }

    @Override
    public void onDragEnd() {
        mActive = false;
    }

    public void setPageIndex(int index) {
        pageIndex = index;
        setText(String.valueOf(pageIndex + 1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View v) {
        mLauncher.getWorkspace().snapToPage(pageIndex);
    }

    @Override
    public boolean isDropEnabled() {
        return pageIndex!=mLauncher.getWorkspace().getNextPage();
    }

    @Override
    public void onDrop(DragObject dragObject) {
    }

    @Override
    public void onDragEnter(DragObject dragObject) {
    }

    @Override
    public void onDragOver(DragObject dragObject) {
        mActive=false;
        mLauncher.getWorkspace().snapToPage(pageIndex);
    }

    @Override
    public void onDragExit(DragObject dragObject) {
        mActive=true;
    }

    @Override
    public void onFlingToDelete(DragObject dragObject, PointF vec) {
    }

    @Override
    public boolean acceptDrop(DragObject dragObject) {
        return true;
    }

    @Override
    public void prepareAccessibilityDrop() {
    }

    @Override
    public void getHitRectRelativeToDragLayer(Rect outRect) {
        super.getHitRect(outRect);
//        outRect.bottom += mBottomDragPadding;
//
        int[] coords = new int[2];
        mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, coords);
        outRect.offsetTo(coords[0], coords[1]);
    }
}
