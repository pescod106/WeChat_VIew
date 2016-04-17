package com.pescod.wechat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by pescod on 4/16/2016.
 */
public class ChangeColorIconWithText extends View {
    private int mColor = 0xff45c01a;
    private String mText = "wechat";
    private Bitmap mIconBitMap;
    private int mTextSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Paint mTextPaint;
    private float mAlpha;
    private Rect mIconRect;
    private Rect mTextBound;

    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS,super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA,mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATUS_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public ChangeColorIconWithText(Context context) {
        this(context,null);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 获取自定义属性的值
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ChangeColorIconWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedValue = context.obtainStyledAttributes(
                attrs,R.styleable.ChangeColorIconWithText);

        int count = typedValue.getIndexCount();
        for (int i=0;i<count;i++){
            int attr = typedValue.getIndex(i);
            switch (attr){
                case R.styleable.ChangeColorIconWithText_wechat_icon:
                    BitmapDrawable bitmapDrawable = (BitmapDrawable)typedValue.getDrawable(attr);
                    mIconBitMap = bitmapDrawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithText_wechat_color:
                    mColor = typedValue.getColor(attr,0xff45c01a);
                    break;
                case R.styleable.ChangeColorIconWithText_wechat_text:
                    mText = typedValue.getString(attr);
                    break;
                case R.styleable.ChangeColorIconWithText_wechat_text_size:
                    mTextSize = (int) typedValue.getDimension(attr, TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                                    getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }
        typedValue.recycle();

        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.getTextBounds(mText,0,mText.length(),mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int iconWidth = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                getMeasuredHeight()-getPaddingTop()-getPaddingBottom()-mTextBound.height());

        int left = (getMeasuredWidth()-iconWidth)/2;
        int top = (getMeasuredHeight()-mTextBound.height()-iconWidth)/2;

        mIconRect = new Rect(left,top,left+iconWidth,top+iconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mIconBitMap,null,mIconRect,null);
        int alpha = (int) Math.ceil(255*mAlpha);
        //内存去准备mBitmap，setAlpha,纯色，xfermode，图标
        settargetBitmap(alpha);
        //绘制原文本，绘制变色的文本
        drawSourceText(canvas,alpha);
        drawTargetText(canvas,alpha);
        canvas.drawBitmap(mBitmap,0,0,null);

    }

    /**
     * 绘制图标
     * @param alpha
     */
    private void settargetBitmap(int alpha){
        mBitmap =  Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight()
        , Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect,mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitMap,null,mIconRect,mPaint);
    }

    /**
     * 绘制原文本
     * @param canvas
     * @param alpha
     */
    private void drawSourceText(Canvas canvas,int alpha){
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha(255-alpha);
        int x,y;
        x = (getMeasuredWidth()-mTextBound.width())/2;
        y = mIconRect.bottom+mTextBound.height();
        canvas.drawText(mText,x,y,mTextPaint);
    }

    private void drawTargetText(Canvas canvas,int alpha){
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        int x,y;
        x = (getMeasuredWidth()-mTextBound.width())/2;
        y = mIconRect.bottom+mTextBound.height();
        canvas.drawText(mText,x,y,mTextPaint);
    }

    public void setIconAlpha(float alpha){
        this.mAlpha = alpha;
        invalidateView();
    }

    /**
     *
     */
    private void invalidateView(){
        if (Looper.getMainLooper()==Looper.myLooper()){
            invalidate();
        }else{
            postInvalidate();
        }
    }

}
