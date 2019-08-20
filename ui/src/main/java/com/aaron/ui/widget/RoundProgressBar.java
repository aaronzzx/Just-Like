package com.aaron.ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.aaron.ui.R;
import com.blankj.utilcode.util.ConvertUtils;

/**
 * 一个圆形进度条，用于开屏广告展示进度
 *
 * @author Aaron Zheng
 * @since 2019.04.19
 */
public class RoundProgressBar extends View {

    // 这一块作为控件默认属性，在使用者没有对相应属性进行赋值的情况下
    private static final int RING_COLOR = Color.parseColor("#4D000000");
    private static final int PROGRESS_COLOR = Color.parseColor("#FFDDAE44");
    private static final int RING_WIDTH = ConvertUtils.dp2px(3);
    private static final int TEXT_SIZE = ConvertUtils.sp2px(10);
    private static final int TEXT_COLOR = Color.WHITE;
    private static final int WIDTH = ConvertUtils.dp2px(35);
    private static final int MAX_PROGRESS = 100;
    private static final int CUR_PROGRESS = 0;
    private static final String TEXT = "跳过";

    private static boolean mIsSkip; // 标记位，表示是否点击了跳过

    private int mRingColor; // 圆环颜色
    private int mProgressColor; // 圆环进度条颜色
    private int mRingWidth; // 圆环宽度

    private int mTextSize; // 字体大小
    private int mTextColor; // 字体颜色
    private String mText; // 内容

    private int mMaxProgress; // 最大进度
    private int mCurProgress; // 当前进度

    private Paint mRingPaint; // 圆环画笔
    private Paint mProgressPaint; // 圆环进度画笔
    private Paint mTextPaint; // 文字画笔

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 因为在绘制的时候需要判断 Activity 已被销毁，所以当使用者传入
        // 非 Activity 的 Context 时抛出异常
        if (!(context instanceof Activity))
            throw new IllegalArgumentException("Context must be activity.");
        mIsSkip = false;
        // 初始化 View 的参数
        init(context, attrs);
    }

    /**
     * 由于是继承自 View ，所以肯定是需要重写 onMeasure() 方法了
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // draw ring
        float circleX = (float) getWidth() / 2; // 确定圆环的中心点
        float circleY = (float) getWidth() / 2; // 确定圆环的中心点
        // 确定半径，需要注意的是圆环的宽度并不一定等于 View 的宽度，
        // 因为环是有厚度的，在计算半径时需要减去环宽度的一半
        float radius = (float) getWidth() / 2 - (float) mRingWidth / 2;
        canvas.drawCircle(circleX, circleY, radius, mRingPaint);

        // draw progress ring
        float sweepAngle = (float) mCurProgress / mMaxProgress * 360; // 绘制当前进度
        // 4 个坐标点，因为弧需要通过矩形来确定自身的位置与大小
        float leftTop = (float) mRingWidth / 2;
        float rightBottom = getWidth() - leftTop;
        // 创建确定弧位置大小的矩形
        RectF oval = new RectF(leftTop, leftTop, rightBottom, rightBottom);
        // -90 表示在时钟的 0 点开始绘制，sweepAngle 就是绘制范围，
        // useCenter 为 false 表示不以扇形绘制
        canvas.drawArc(oval, -90, sweepAngle, false, mProgressPaint);

        // draw text
        // 包含全部文本的最小矩形
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
        // x 和 y 决定在 View 的哪个位置开始绘制，文本的绘制是在矩形的左下角开始的
        float x = (float) getWidth() / 2 - (float) bounds.width() / 2;
        float y = (float) getWidth() / 2 + (float) bounds.height() / 2;
        canvas.drawText(mText, x, y, mTextPaint);
    }

    /**
     * 设置点击监听器，与 setOnClickListener 区别在于形参是实现了 OnClickListener 的抽象类，
     * 在实现的 onClick(View v) 中设置了被点击标记位，另需要使用者实现抽象方法。
     *
     * @param listener 实现了 OnClickListener 的抽象类
     */
    public void setOnPressListener(OnPressListener listener) {
        setOnClickListener(listener);
    }

    /**
     * 进度条开始滑动
     *
     * @param countDown 倒计时具体毫秒后停止滑动，主要用来控制滑动速度
     */
    public void startSlide(long countDown, SlideCallback callback) {
        // 每 mMaxProgress 分之一的进度需要休眠多长毫秒
        long sleep = countDown / mMaxProgress;
        new Thread(() -> {
            // 循环设置当前进度，如果没有休眠的话是看不到进度滑动的
            for (int i = 0; i < mMaxProgress; i++) {
                // 如果被点击或 Context 已销毁则跳出循环停止滑动，并重新赋值 mIsSkip 为 false
                // 避免因持有 Context 而造成内存泄漏
                if (mIsSkip || ((Activity) getContext()).isFinishing()) {
                    mIsSkip = false;
                    return;
                }
                SystemClock.sleep(sleep); // 开始休眠
                this.setCurProgress(i + 1); // 设置当前进度，在 onDraw() 中通过这个去绘制进度
                this.postInvalidate(); // 通知 View 进行绘制
                // 将当前进度回调给调用方，调用方可根据当前进度来实现具体逻辑
                // 使用 post（）是因为在回调在主线程发出后，调用方就不用再去切换回主线程了
                this.post(() -> callback.onProgress(mCurProgress, mMaxProgress));
            }
        }).start();
    }

    /**
     * 初始化自定义属性
     */
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            // 这里属于 View 的自定义属性，通过使用者在 layout 文件中写入的参数进行赋值
            // 如果没有主动赋值则使用 View 的默认参数进行赋值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
            mRingColor = typedArray.getColor(R.styleable.RoundProgressBar_ringColor, RING_COLOR);
            mProgressColor = typedArray.getColor(R.styleable.RoundProgressBar_progressColor, PROGRESS_COLOR);
            mRingWidth = (int) typedArray.getDimension(R.styleable.RoundProgressBar_ringWidth, RING_WIDTH);

            String text = typedArray.getString(R.styleable.RoundProgressBar_text);
            mText = text != null ? text : TEXT;
            mTextSize = (int) typedArray.getDimension(R.styleable.RoundProgressBar_textSize, TEXT_SIZE);
            mTextColor = typedArray.getColor(R.styleable.RoundProgressBar_textColor, TEXT_COLOR);

            mMaxProgress = typedArray.getInteger(R.styleable.RoundProgressBar_maxProgress, MAX_PROGRESS);
            mCurProgress = typedArray.getInteger(R.styleable.RoundProgressBar_curProgress, CUR_PROGRESS);

            typedArray.recycle();
        } else {
            // 这一段用于使用者通过 Java 代码直接创建 View 后进行默认参数赋值
            mRingColor = RING_COLOR;
            mProgressColor = PROGRESS_COLOR;
            mRingWidth = RING_WIDTH;

            mText = TEXT;
            mTextSize = TEXT_SIZE;
            mTextColor = TEXT_COLOR;

            mMaxProgress = MAX_PROGRESS;
            mCurProgress = CUR_PROGRESS;
        }
        initUtils(); // 初始化工具，如画笔
    }

    /**
     * 初始化画笔等工具
     */
    private void initUtils() {
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true); // 开启抗锯齿
        mRingPaint.setStyle(Paint.Style.FILL); // FILL 表示绘制实心，STROKE 表示绘制空心
        mRingPaint.setColor(mRingColor);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mRingWidth);
        mProgressPaint.setColor(mProgressColor);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStrokeWidth(0);
    }

    /**
     * 由于直接继承 View ，为了避免在使用 wrap_content 时 View 无限大，因此需要重新测量大小
     * 这里没什么说的，继承自 View 的都需要自己测量大小
     */
    private int measureSize(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = WIDTH;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    // 下面为 getter 和 setter 方法，为 View 自定义属性的主动赋值修改与获取，可动态更改属性
    public int getRingColor() {
        return mRingColor;
    }

    public void setRingColor(int ringColor) {
        mRingColor = ringColor;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public int getRingWidth() {
        return mRingWidth;
    }

    public void setRingWidth(int ringWidth) {
        mRingWidth = ringWidth;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public int getCurProgress() {
        return mCurProgress;
    }

    public void setCurProgress(int curProgress) {
        mCurProgress = curProgress;
    }

    /**
     * 自定义点击监听器，在实现方法内加入被点击标记位
     */
    public static abstract class OnPressListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            v.setOnClickListener(null);
            mIsSkip = true;
            onPress(v);
        }

        public abstract void onPress(View view);
    }

    /**
     * 回调滑动进度
     */
    public interface SlideCallback {

        void onProgress(int curProgress, int maxProgress);
    }
}
