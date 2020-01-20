package com.daasuu.epf;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.daasuu.epf.callbacks.QuardGridFilterCallback;
import com.daasuu.epf.chooser.EConfigChooser;
import com.daasuu.epf.contextfactory.EContextFactory;
import com.daasuu.epf.filter.GlFilter;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.video.VideoListener;


public class EPlayerView extends GLSurfaceView implements VideoListener, QuardGridFilterCallback {

    private final static String TAG = EPlayerView.class.getSimpleName();
    public static int screenwidth;
    public static int screenheight;

    private final EPlayerRenderer renderer;
    private SimpleExoPlayer player;

    private float videoAspect = 1f;
    private PlayerScaleType playerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH;

    public EPlayerView(Context context) {
        this(context, null);
    }

    class control{
        private LinearLayout upLayout;
        private LinearLayout downLayout;
        private ImageView upView;
        private ImageView downView;
        private RelativeLayout angleLayout;
        private RelativeLayout listLayout;
        public void initView(LinearLayout ul, LinearLayout dl, ImageView uv, ImageView dv, RelativeLayout angle, RelativeLayout list){
            upLayout = ul; downLayout = dl; upView = uv; downView = dv; angleLayout = angle; listLayout = list;
        }
        public void setBannerVisible(){
            upLayout.setVisibility(View.VISIBLE); downLayout.setVisibility(View.VISIBLE);
            upView.setVisibility(View.VISIBLE); downView.setVisibility(View.VISIBLE);
            upLayout.setAlpha(1); downLayout.setAlpha(1);
            upView.setImageAlpha(255); downView.setImageAlpha(255);
            upView.bringToFront(); downView.bringToFront();
            upLayout.bringToFront(); downLayout.bringToFront();
        }
        public void setBannerInvisible(){
            upLayout.setVisibility(View.INVISIBLE); downLayout.setVisibility(View.INVISIBLE);
            upView.setVisibility(View.INVISIBLE); downView.setVisibility(View.INVISIBLE);
            upLayout.setAlpha(0); downLayout.setAlpha(0);
            upView.setImageAlpha(0); downView.setImageAlpha(0);
        }
        public void setRightInvisible(){
            angleLayout.setVisibility(View.INVISIBLE);
            listLayout.setVisibility(View.INVISIBLE);
            angleLayout.setAlpha(0);
            listLayout.setAlpha(0);
        }
    }
    public control ctr = new control();

    private int viewIndex;
    private int viewTotalNum = 0;

    private boolean isPause;
    public boolean isLocked = true;
    //isClicked=0时，代表了上下banner的状态，1为控件都不可见的情况，2是有右侧layout的情况
    public int isClicked = 0;


    public EPlayerView(Context context, final int viewNumPerRow, final int viewNumPerCol, QuardGridFilterCallback quardGridFilterCallback) {
        super(context, null);

        isPause = false;
        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this, viewNumPerRow, viewNumPerCol, this);
        setRenderer(renderer);
        viewIndex = viewNumPerRow * viewNumPerCol / 2;
        viewTotalNum = viewNumPerRow * viewNumPerCol - 1;

        setOnTouchListener(new OnTouchListener() {

            private float mPosX, mPosY, mCurPosX, mCurPosY, lastMovX, lastMovY, pressedX;

            int direction = 0; //0 left, 1 right;
            int pressedViewIndex = -1;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float moveSensitivity = 0.05f;

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        pressedX = mPosX;
                        lastMovX = mPosX;
                        lastMovY = mPosY;
                        pressedViewIndex = viewIndex;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        if(mCurPosX > lastMovX){
                            if(direction == 0){
                                mPosX = lastMovX;
                                mPosY = lastMovY;
                                pressedViewIndex = viewIndex;
                                direction = 1;
                            }
                        }
                        else if(mCurPosX < lastMovX){
                            if(direction == 1){
                                mPosX = lastMovX;
                                mPosY = lastMovY;
                                pressedViewIndex = viewIndex;
                                direction = 0;
                            }
                        }
                        float moveDistance = Math.abs(mCurPosX - mPosX);
                        if(isLocked) break;
                        float changeIndex = moveDistance * moveSensitivity;
                        if(mCurPosX > mPosX){
                            viewIndex = pressedViewIndex + (int)changeIndex;
                        }
                        else{
                            viewIndex = pressedViewIndex - (int)changeIndex;
                        }
                        if(viewIndex > viewTotalNum){
                            viewIndex = viewTotalNum;
                        }
                        else if(viewIndex < 0){
                            viewIndex = 0;
                        }
//                        System.out.println("changeIndex "+ changeIndex +" viewIndex "+viewIndex);
                        lastMovX = mCurPosX;
                        lastMovY = mCurPosY;
                        break;

                    case MotionEvent.ACTION_UP:

                        if(Math.abs(event.getX() - pressedX)>10){
                            break;
                        }

//                        if (player == null) break;
//                        if (isPause) {
//                            player.setPlayWhenReady(true);
//                            isPause = false;
//                        } else {
//                            player.setPlayWhenReady(false);
//                            isPause = true;
//                        }

                        if(isClicked==0){
                            ctr.setBannerInvisible();;
                            isClicked = 1;
                        }else if(isClicked==1){
                            ctr.setBannerVisible();
                            isClicked = 0;
                        }else if(isClicked==2){
                            ctr.setRightInvisible();
                            ctr.setBannerVisible();
                            isClicked = 0;
                        }
                        break;

                }
                return true;
            }
        });

    }

    public void initAll(LinearLayout up, LinearLayout down, ImageView top, ImageView btm, RelativeLayout angle, RelativeLayout list){
        ctr.initView(up,down,top,btm,angle,list);
    }

    @Override
    public int getNewViewIndex() {

        return viewIndex;
    }

    public void slideLock(){
        isLocked = !isLocked;
    }

    public EPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this);
        setRenderer(renderer);

    }

    public EPlayerView setSimpleExoPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addVideoListener(this);
        this.renderer.setSimpleExoPlayer(player);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    public void setPlayerScaleType(PlayerScaleType playerScaleType) {
        this.playerScaleType = playerScaleType;
        requestLayout();
    }

    public int getCurrentFrameIndex(){
        return (int)this.player.getCurrentPosition()*30/1000;
    }

    public void changeViewIndex(int index){
        viewIndex = index;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int orientation = getResources().getConfiguration().orientation;
        int measuredWidth = 1500;
        int measuredHeight = 3000;
        //if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
           measuredWidth = getMeasuredWidth();
           measuredHeight = getMeasuredHeight();
           screenheight = getMeasuredHeight();
           screenwidth = getMeasuredWidth();
        //}
        //Log.v("eplayerview size: ",getMeasuredHeight()+" "+getMeasuredWidth());

        int viewWidth = measuredWidth;
        int viewHeight = measuredHeight;

        switch (playerScaleType) {
            case RESIZE_FIT_WIDTH:
                viewHeight = (int) (measuredWidth / videoAspect);

                break;
            case RESIZE_FIT_HEIGHT:
                viewWidth = (int) (measuredHeight * videoAspect);

                break;
        }

         Log.d(TAG, "onMeasure viewWidth = " + screenwidth + " viewHeight = " + screenheight);

        setMeasuredDimension(viewWidth, viewHeight);

    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        // Log.d(TAG, "width = " + width + " height = " + height + " unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
        videoAspect = ((float) width / height) * pixelWidthHeightRatio;
        // Log.d(TAG, "videoAspect = " + videoAspect);
        requestLayout();
    }

    @Override
    public void onRenderedFirstFrame() {
        // do nothing
    }
}
