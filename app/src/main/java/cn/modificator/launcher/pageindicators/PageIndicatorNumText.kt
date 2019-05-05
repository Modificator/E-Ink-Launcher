package cn.modificator.launcher.pageindicators

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.modificator.launcher.DropTarget
import cn.modificator.launcher.Launcher
import cn.modificator.launcher.R
import cn.modificator.launcher.dragndrop.DragController
import cn.modificator.launcher.dragndrop.DragOptions
import cn.modificator.launcher.settings.MainSetting
import cn.modificator.launcher.settings.ui.SettingsActivity
import cn.modificator.launcher.util.hide
import cn.modificator.launcher.util.show

class PageIndicatorNumText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        PageIndicator(context, attrs, defStyleAttr), DragController.DragListener {

    private var pageIndex = 0
    private val triangleWidth: Float
    private val triangleHeight: Float
    private lateinit var dragNumIndicator: LinearLayout
    private lateinit var normalNumIndicator: LinearLayout
    private lateinit var tvPageIndex: TextView
    private lateinit var tvPageCount: TextView
    private var isDragging = false
    private var actionSetting: ImageView
    private var trianglePath: Path
    private var trianglePaint: Paint

    init {
        val metrics = resources.displayMetrics
        triangleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, metrics)
        triangleHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, metrics)

        trianglePath = Path()
        trianglePath.moveTo(-triangleWidth / 2f, height.toFloat())
        trianglePath.lineTo(0f, height - triangleHeight)
        trianglePath.lineTo(triangleWidth / 2f, height.toFloat())
        trianglePath.close()

        trianglePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        trianglePaint.color = Color.BLACK

        setBackgroundColor(Color.WHITE);

        actionSetting = ImageView(context).apply {
            setImageResource(R.drawable.action_icon_settings)
            layoutParams = FrameLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.bottom_bar_height), resources.getDimensionPixelSize(R.dimen.bottom_bar_height), Gravity.RIGHT)
            setPadding(convertDpToPixel(10f), convertDpToPixel(10f), convertDpToPixel(10f), convertDpToPixel(10f))
            setOnLongClickListener { context.startActivity(Intent(context, SettingsActivity::class.java));false }
            setOnClickListener { MainSetting(context).showAsDropDown(this@PageIndicatorNumText) }
            addView(this)
        }
        init()
    }

    private fun init() {
        dragNumIndicator = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            hide()
            this@PageIndicatorNumText.addView(this, FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
        }
        normalNumIndicator = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            this@PageIndicatorNumText.addView(this, FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT))
            tvPageIndex = TextView(context)
            tvPageIndex.textSize = 35f
            tvPageIndex.includeFontPadding = false
            tvPageIndex.setTextColor(Color.BLACK)
            addView(tvPageIndex, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP or Gravity.LEFT
                leftMargin = convertDpToPixel(8f)
            })

            var tvDivider = TextView(context)
            tvDivider.text = "/"
            tvDivider.textSize = 38f
            tvDivider.scaleY = 1.5f
            tvDivider.setTextColor(Color.BLACK)
            addView(tvDivider, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER_VERTICAL
            })

            tvPageCount = TextView(context)
            tvPageCount.textSize = 35f
            tvPageCount.includeFontPadding = false
            tvPageCount.setTextColor(Color.BLACK)
            addView(tvPageCount, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.BOTTOM
            })
        }
    }

    fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

    override fun setActiveMarker(activePage: Int) {
        super.setActiveMarker(activePage)
        pageIndex = activePage
        post {
            tvPageIndex.text = (pageIndex + 1).toString()
        }
        postInvalidateOnAnimation()
    }

    override fun addMarker() {
        val pageNumDragTarget = PageNumDragTarget(context)
        dragNumIndicator.addView(pageNumDragTarget, LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        val launcher = Launcher.getLauncher(context)
        launcher.dragController.addDropTarget(pageNumDragTarget)
        launcher.dragController.addDragListener(pageNumDragTarget)
        pageNumDragTarget.setLauncher(launcher)
        super.addMarker()
    }

    override fun removeMarker() {
        val removeView = dragNumIndicator.getChildAt(mNumPages - 1) as PageNumDragTarget
        dragNumIndicator.removeView(removeView)
        val launcher = Launcher.getLauncher(context)
        launcher.dragController.removeDropTarget(removeView)
        launcher.dragController.removeDragListener(removeView)
        super.removeMarker()
    }

    override fun onPageCountChanged() {
        super.onPageCountChanged()
        for (i in 0 until dragNumIndicator.childCount) {
            if (dragNumIndicator.getChildAt(i) is PageNumDragTarget) {
                val pageNumDragTarget = dragNumIndicator.getChildAt(i) as PageNumDragTarget
                pageNumDragTarget.setPageIndex(i)
            }
        }
        tvPageCount.text = mNumPages.toString()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDragging) {
            val currCenterX = height / 2f + (pageIndex * height).toFloat()
            canvas.save()
            canvas.translate(currCenterX, height.toFloat())
            trianglePaint.strokeWidth = 1f
            canvas.drawPath(trianglePath, trianglePaint)
            canvas.restore()
        }
        trianglePaint.strokeWidth = convertDpToPixel(2f).toFloat()
        canvas.drawLine(0F, 0F, width.toFloat(), 0F, trianglePaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        Launcher.getLauncher(context).dragController.addDragListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Launcher.getLauncher(context).dragController.removeDragListener(this)
    }

    override fun onDragStart(dragObject: DropTarget.DragObject, options: DragOptions) {
        isDragging = true
        dragNumIndicator.show()
        actionSetting.hide()
        normalNumIndicator.hide()
    }

    override fun onDragEnd() {
        isDragging = false
        dragNumIndicator.hide()
        actionSetting.show()
        normalNumIndicator.show()
    }
}
