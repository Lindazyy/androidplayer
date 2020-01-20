package com.daasuu.exoplayerfilter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.daasuu.epf.EPlayerView;

import java.util.ArrayList;
import java.util.List;

public class CalibrationView extends View {

    class EView{
        private EPlayerView view;
        private TextView angleText;
        public void setView(EPlayerView v, TextView tv){
            view = v; angleText = tv;
        }
        public void setVideoIndex(int i){
            view.changeViewIndex(i);
            angleText.setText("视频视角："+(6-getCurrentPosition())+"");
        }
    }
    EView eView = new EView();
    int viewW = 300;
    int viewH = 300;
    private float centerY = 100;//中心点y
    private int arcWidth = 200;//弧形的宽度，通过paint的setStrokeWidth来设置
    private float radius;//弧长半径

    private int DEF_VIEW_SIZE = 300;//默认view大小
    private Paint p_bc, p_circle, p_midCircle;


    private List<CircleLitem> circleList;

    private int nowPosition = 3;//默认显示在中间位置
    private int notSize = 5;
    private int circleSize = 5;

    public CalibrationView(Context context) {
        super(context);


    }

    public CalibrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        p_bc = new Paint();
        p_bc.setColor(Color.BLACK);
        p_bc.setStyle(Paint.Style.STROKE);
        p_bc.setAntiAlias(true);//去噪
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewH = h;
        viewW = w;
        centerY = h;//中心点y坐标
        arcWidth = h / 5;//圆弧宽带
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = 0;
        int heightSize = 0;

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        } else {
            widthSize = getMeasuredWidth();
        }

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEF_VIEW_SIZE, getResources().getDisplayMetrics());
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        } else {
            heightSize = getMeasuredHeight();
        }

        if (widthSize != 0 && heightSize != 0 && widthSize / 2 < heightSize) {//画的是上半圆弧，所以需要宽度超过高度的2倍保证画出来的图像不留空隙
            heightSize = widthSize;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i("TestView","onDraw");
//        RectF rf = new RectF(0, 0, viewW, viewH);
//        RectF rf1 = new RectF(100, 100, 1000, 2000);
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);


        float x = (getWidth() - getHeight() / 2) / 2;
        float y = getHeight() / 4;

        Log.e("cali", "x=" + x);
        Log.e("cali", "y=" + y);
        Log.e("cali", "width=" + getWidth());
        Log.e("cali", "height=" + getHeight());

        radius = y;
        centerY = 2 * y;

        RectF oval = new RectF(x, y,
                getWidth() - x, getHeight() - y);
        canvas.drawRect(oval, p);
        drawBg(canvas, oval);//画背景弧形和圆点
        canvas.save();
    }

    /**
     * 画背景灰色圆形和点
     *
     * @param canvas
     * @param rf
     */
    private void drawBg(Canvas canvas, RectF rf) {

        p_bc = new Paint();
        p_bc.setColor(Color.GRAY);
        p_bc.setStyle(Paint.Style.STROKE);
        p_bc.setAntiAlias(true);//去噪

        p_bc.setStrokeWidth(20);
        p_bc.setAlpha(100);
        canvas.drawArc(rf, 0, 180, false, p_bc);//从-180度开始画一段180度的弧形，0度是3点钟方向
        drawPoint(rf, canvas, nowPosition);

    }

    /**
     * 画刻度点
     *
     * @param rf
     * @param canvas
     */
    private void drawPoint(RectF rf, Canvas canvas, int nowPosition) {
        /**
         * 画180度的上弧形作为刻度盘
         * 分成10分，每份18度
         * 刻度点只需要画1-9个，即最边上的两个不需要画
         *
         * 已知圆弧的半径，可以根据知道直角三角形斜边及角C，计算出每个刻度的的位置，然后画点
         *
         * ps  直角三角形已知斜边c和对应角，其他两边分别为c*sin(C)与c*cos(C)
         */
        circleList = new ArrayList<>();
        float x = 900.0f;
        for (int i = 1; i < 1 + circleSize; i++) {
            int d = i * (180 / (circleSize + 1));
            int deg = d;
            if (d > 90) {
                deg = 180 - d;
            }

            float degree = (float) (Math.PI * (deg * 1.0f / 180));
            float y = (float) (centerY + (radius * 1.0f * Math.sin(degree)));

            if (d > 90) {
                x = (float) (centerY - (radius * 1.0f * Math.cos(degree)));
            } else {
                x = (float) (centerY + (radius * 1.0f * Math.cos(degree)));
            }
            CircleLitem circleLitem = new CircleLitem();
            circleLitem.setX(x);
            circleLitem.setY(y);
            circleList.add(circleLitem);
            if (i == nowPosition) {
                p_circle = new Paint();
                p_circle.setColor(Color.BLUE);
                p_midCircle = new Paint();
                p_midCircle.setColor(Color.WHITE);
                canvas.drawCircle(x, y, 7 * notSize, p_circle);
                canvas.drawCircle(x, y, 4 * notSize, p_midCircle);

            } else {
                p_circle = new Paint();
                p_circle.setColor(Color.WHITE);
                p_midCircle = new Paint();
                p_midCircle.setColor(Color.GRAY);
                canvas.drawCircle(x, y, 6 * notSize, p_circle);
                canvas.drawCircle(x, y, 2 * notSize, p_midCircle);
            }


//            canvas.drawPoint(x,y,p_circle);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (int i = 0; i < circleList.size(); i++) {
            CircleLitem circleLitem = circleList.get(i);
            if ((circleLitem.getX() + 6 * notSize > x && circleLitem.getX() - 6 * notSize < x) && (circleLitem.getY() + 6 * notSize > y && circleLitem.getY() - 6 * notSize < y)) {
                nowPosition = i + 1;
                eView.setVideoIndex(nowPosition==1?0:nowPosition*36/5-4);
                postInvalidate();
            }
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }


    public void setCurrentPosition(int i) {
        nowPosition = i;
        postInvalidate();
    }

    public int getCurrentPosition() {
        return nowPosition;
    }

    public int getNotSize() {
        return notSize;
    }

    public void setView(EPlayerView v, TextView tv){
        eView.setView(v,tv);
    }

    public void setNotSize(int notSize) {
        this.notSize = notSize;
    }

    public int getCircleSize() {
        return circleSize;
    }

    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
        if (circleSize % 2 == 1) {
            nowPosition = (int) (circleSize + 1) / 2;
        } else {
            nowPosition = (int) circleSize / 2;
        }

    }

    public class CircleLitem {
        private float x;
        private float y;

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }


}
