package com.daasuu.exoplayerfilter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.daasuu.epf.EPlayerView;
import com.google.android.exoplayer2.util.Log;

public class BoneSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    class EView{
        private EPlayerView view;
        public void setView(EPlayerView v){
            view = v;
        }
        public int getCurrentCamNum(){
            return view.getNewViewIndex();
        }
        public int getCurrentFrameCnt(){
            return view.getCurrentFrameIndex();
        }
        public boolean isNull(){
            if(view==null) return true;
            else return false;
        }
    }
    EView eView = new EView();

    private Timer timer;
    private float[][][][] kps = new float[800][53][25][2];

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openFile(String name){
        try {
            byte[] allBytes = Files.readAllBytes(Paths.get(name));
            ByteBuffer buffer = ByteBuffer.wrap(allBytes).order(ByteOrder.LITTLE_ENDIAN);
            for (int frame = 0; frame < 800; ++frame) {
                for (int cam = 0; cam < 53; ++cam) {
                    for (int point = 0; point < 25; ++point) {
                        for (int xy = 0; xy < 2; ++xy) {
                            kps[frame][cam][point][xy] = buffer.getFloat();
                        }
                    }
                }
            }
            System.out.println("fin");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setView(EPlayerView v){
        eView.setView(v);
    }

    public BoneSurfaceView(Context context) {
        super(context);
        init();
    }

    public BoneSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BoneSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Paint mPaint;
    private boolean lock = true;

    void init(){

        Log.e("EPlayerView","start init...");
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
        setZOrderOnTop(true);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                    //Log.e("surfaceview","time up!");
                    if(eView==null||eView.isNull()) return;
                    draw(eView.getCurrentFrameCnt(), eView.getCurrentCamNum());
            }
        }, 30,20);

        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                openFile(Environment.getExternalStorageDirectory().toString()+"/DCIM/pts.bin");
            }
        }).start();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Log.e("EPlayerView","stop init...");
    }

    public void ctrRender(boolean flag){
        lock = flag;
    }

    void draw(int frameCnt, int camCnt){
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(4);
            mPaint.setStyle(Paint.Style.FILL);

            mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

            if(lock){
                return;
            }
            
            int num = camCnt%2==1? camCnt/2*3+1: camCnt/2*3;


            // 0 draw point
            mCanvas.drawCircle(kps[frameCnt][num][0][0]*1795+140, kps[frameCnt][num][0][1]*1167, 12, mPaint);

            // 1 draw point

            mCanvas.drawCircle(kps[frameCnt][num][1][0]*1795+140, kps[frameCnt][num][1][1]*1167, 12, mPaint);

            // 2 draw point
            mPaint.setColor(Color.rgb(202,67,0));
            mCanvas.drawCircle(kps[frameCnt][num][2][0]*1795+140, kps[frameCnt][num][2][1]*1167, 12, mPaint);

            // 3 draw point
            mPaint.setColor(Color.rgb(153,102,0));
            mCanvas.drawCircle(kps[frameCnt][num][3][0]*1795+140, kps[frameCnt][num][3][1]*1167, 12, mPaint);

            // 4 draw point
            mPaint.setColor(Color.rgb(153,153,0));
            mCanvas.drawCircle(kps[frameCnt][num][4][0]*1795+140, kps[frameCnt][num][4][1]*1167, 12, mPaint);

            // 5 draw point
            mPaint.setColor(Color.rgb(102,153,0));
            mCanvas.drawCircle(kps[frameCnt][num][5][0]*1795+140, kps[frameCnt][num][5][1]*1167, 12, mPaint);

            // 6 draw point
            mPaint.setColor(Color.rgb(51,153,0));
            mCanvas.drawCircle(kps[frameCnt][num][6][0]*1795+140, kps[frameCnt][num][6][1]*1167, 12, mPaint);

            // 7 draw point
            mPaint.setColor(Color.rgb(0,153,0));
            mCanvas.drawCircle(kps[frameCnt][num][7][0]*1795+140, kps[frameCnt][num][7][1]*1167, 12, mPaint);

            // 8 draw point
            mPaint.setColor(Color.rgb(153,0,0));
            mCanvas.drawCircle(kps[frameCnt][num][8][0]*1795+140, kps[frameCnt][num][8][1]*1167, 12, mPaint);

            // 9 draw point
            mPaint.setColor(Color.rgb(0,153,51));
            mCanvas.drawCircle(kps[frameCnt][num][9][0]*1795+140, kps[frameCnt][num][9][1]*1167, 12, mPaint);

            // 10 draw point
            mPaint.setColor(Color.rgb(0,153,102));
            mCanvas.drawCircle(kps[frameCnt][num][10][0]*1795+140, kps[frameCnt][num][10][1]*1167, 12, mPaint);

            // 11 draw point
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawCircle(kps[frameCnt][num][11][0]*1795+140, kps[frameCnt][num][11][1]*1167, 12, mPaint);

            // 12 draw point
            mPaint.setColor(Color.rgb(0,102,153));
            mCanvas.drawCircle(kps[frameCnt][num][12][0]*1795+140, kps[frameCnt][num][12][1]*1167, 12, mPaint);

            // 13 draw point
            mPaint.setColor(Color.rgb(0,51,153));
            mCanvas.drawCircle(kps[frameCnt][num][13][0]*1795+140, kps[frameCnt][num][13][1]*1167, 12, mPaint);

            // 14 draw point
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][14][0]*1795+140, kps[frameCnt][num][14][1]*1167, 12, mPaint);

            // 15 draw point
            mPaint.setColor(Color.rgb(153,0,102));
            mCanvas.drawCircle(kps[frameCnt][num][15][0]*1795+140, kps[frameCnt][num][15][1]*1167, 12, mPaint);

            // 16 draw point
            mPaint.setColor(Color.rgb(102,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][16][0]*1795+140, kps[frameCnt][num][16][1]*1167, 12, mPaint);

            // 17 draw point
            mPaint.setColor(Color.rgb(153,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][17][0]*1795+140, kps[frameCnt][num][17][1]*1167, 12, mPaint);

            // 18 draw point
            mPaint.setColor(Color.rgb(51,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][18][0]*1795+140, kps[frameCnt][num][18][1]*1167, 12, mPaint);

            // 19 draw point
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][19][0]*1795+140, kps[frameCnt][num][19][1]*1167, 12, mPaint);

            // 20 draw point
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][20][0]*1795+140, kps[frameCnt][num][20][1]*1167, 12, mPaint);

            // 21 draw point
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawCircle(kps[frameCnt][num][21][0]*1795+140, kps[frameCnt][num][21][1]*1167, 12, mPaint);

            // 22 draw point
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawCircle(kps[frameCnt][num][22][0]*1795+140, kps[frameCnt][num][22][1]*1167, 12, mPaint);

            // 23 draw point
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawCircle(kps[frameCnt][num][23][0]*1795+140, kps[frameCnt][num][23][1]*1167, 12, mPaint);

            // 24 draw point
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawCircle(kps[frameCnt][num][24][0]*1795+140, kps[frameCnt][num][24][1]*1167, 12, mPaint);

            // draw line
            mPaint.setColor(Color.GREEN);
            mPaint.setStrokeWidth(8);
            mPaint.setStyle(Paint.Style.STROKE);

            // 17-15
            mPaint.setColor(Color.rgb(153,0,153));
            mCanvas.drawLine(kps[frameCnt][num][17][0]*1795+140, kps[frameCnt][num][17][1]*1167, kps[frameCnt][num][15][0]*1795+140, kps[frameCnt][num][15][1]*1167, mPaint);
            // 18-16
            mPaint.setColor(Color.rgb(51,0,153));
            mCanvas.drawLine(kps[frameCnt][num][18][0]*1795+140, kps[frameCnt][num][18][1]*1167, kps[frameCnt][num][16][0]*1795+140, kps[frameCnt][num][16][1]*1167, mPaint);

            // 1-> 0
            mPaint.setColor(Color.rgb(153,0,51));
            mCanvas.drawLine(kps[frameCnt][num][1][0]*1795+140, kps[frameCnt][num][1][1]*1167, kps[frameCnt][num][0][0]*1795+140, kps[frameCnt][num][0][1]*1167, mPaint);
            // 15 -> 0
            mPaint.setColor(Color.rgb(153,0,102));
            mCanvas.drawLine(kps[frameCnt][num][15][0]*1795+140, kps[frameCnt][num][15][1]*1167, kps[frameCnt][num][0][0]*1795+140, kps[frameCnt][num][0][1]*1167, mPaint);
            // 10 -> 0
            //mCanvas.drawLine(kps[frameCnt][num][10][0]*1795+140, kps[frameCnt][num][10][1]*1167, kps[frameCnt][num][0][0]*1795+140, kps[frameCnt][num][0][1]*1167, mPaint);
            // 2-> 1
            mPaint.setColor(Color.rgb(153,51,0));
            mCanvas.drawLine(kps[frameCnt][num][2][0]*1795+140, kps[frameCnt][num][2][1]*1167, kps[frameCnt][num][1][0]*1795+140, kps[frameCnt][num][1][1]*1167, mPaint);
            // 3-> 2
            mPaint.setColor(Color.rgb(153,102,0));
            mCanvas.drawLine(kps[frameCnt][num][3][0]*1795+140, kps[frameCnt][num][3][1]*1167, kps[frameCnt][num][2][0]*1795+140, kps[frameCnt][num][2][1]*1167, mPaint);
            // 4-> 3
            mPaint.setColor(Color.rgb(153,153,0));
            mCanvas.drawLine(kps[frameCnt][num][4][0]*1795+140, kps[frameCnt][num][4][1]*1167, kps[frameCnt][num][3][0]*1795+140, kps[frameCnt][num][3][1]*1167, mPaint);
            // 5-> 1
            mPaint.setColor(Color.rgb(102,153,0));
            mCanvas.drawLine(kps[frameCnt][num][5][0]*1795+140, kps[frameCnt][num][5][1]*1167, kps[frameCnt][num][1][0]*1795+140, kps[frameCnt][num][1][1]*1167, mPaint);
            // 6-> 5
            mPaint.setColor(Color.rgb(51,153,0));
            mCanvas.drawLine(kps[frameCnt][num][6][0]*1795+140, kps[frameCnt][num][6][1]*1167, kps[frameCnt][num][5][0]*1795+140, kps[frameCnt][num][5][1]*1167, mPaint);
            // 7-> 6
            mPaint.setColor(Color.rgb(0,153,0));
            mCanvas.drawLine(kps[frameCnt][num][7][0]*1795+140, kps[frameCnt][num][7][1]*1167, kps[frameCnt][num][6][0]*1795+140, kps[frameCnt][num][6][1]*1167, mPaint);
            // 8-> 1
            mPaint.setColor(Color.rgb(153,0,0));
            mCanvas.drawLine(kps[frameCnt][num][8][0]*1795+140, kps[frameCnt][num][8][1]*1167, kps[frameCnt][num][1][0]*1795+140, kps[frameCnt][num][1][1]*1167, mPaint);
            // 9-> 8
            mPaint.setColor(Color.rgb(0,153,51));
            mCanvas.drawLine(kps[frameCnt][num][9][0]*1795+140, kps[frameCnt][num][9][1]*1167, kps[frameCnt][num][8][0]*1795+140, kps[frameCnt][num][8][1]*1167, mPaint);
            // 10-> 9
            mPaint.setColor(Color.rgb(0,153,102));
            mCanvas.drawLine(kps[frameCnt][num][10][0]*1795+140, kps[frameCnt][num][10][1]*1167, kps[frameCnt][num][9][0]*1795+140, kps[frameCnt][num][9][1]*1167, mPaint);// 1-> 0
            // 11->10
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawLine(kps[frameCnt][num][11][0]*1795+140, kps[frameCnt][num][11][1]*1167, kps[frameCnt][num][10][0]*1795+140, kps[frameCnt][num][10][1]*1167, mPaint);// 1-> 0
            // 24->11
            mPaint.setColor(Color.rgb(0,155,155));
            mCanvas.drawLine(kps[frameCnt][num][24][0]*1795+140, kps[frameCnt][num][24][1]*1167, kps[frameCnt][num][11][0]*1795+140, kps[frameCnt][num][11][1]*1167, mPaint);
            // 22-> 11
            mPaint.setColor(Color.rgb(0,153,153));
            mCanvas.drawLine(kps[frameCnt][num][22][0]*1795+140, kps[frameCnt][num][22][1]*1167, kps[frameCnt][num][11][0]*1795+140, kps[frameCnt][num][11][1]*1167, mPaint);
            // 12->8
            mPaint.setColor(Color.rgb(0,102,153));
            mCanvas.drawLine(kps[frameCnt][num][12][0]*1795+140, kps[frameCnt][num][12][1]*1167, kps[frameCnt][num][8][0]*1795+140, kps[frameCnt][num][8][1]*1167, mPaint);
            // 13->12
            mPaint.setColor(Color.rgb(0,51,153));
            mCanvas.drawLine(kps[frameCnt][num][13][0]*1795+140, kps[frameCnt][num][13][1]*1167, kps[frameCnt][num][12][0]*1795+140, kps[frameCnt][num][12][1]*1167, mPaint);
            // 14->13
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawLine(kps[frameCnt][num][14][0]*1795+140, kps[frameCnt][num][14][1]*1167, kps[frameCnt][num][13][0]*1795+140, kps[frameCnt][num][13][1]*1167, mPaint);
            // 21->14
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawLine(kps[frameCnt][num][21][0]*1795+140, kps[frameCnt][num][21][1]*1167, kps[frameCnt][num][14][0]*1795+140, kps[frameCnt][num][14][1]*1167, mPaint);
            // 20->14
            mPaint.setColor(Color.rgb(0,0,153));
            mCanvas.drawLine(kps[frameCnt][num][20][0]*1795+140, kps[frameCnt][num][20][1]*1167, kps[frameCnt][num][14][0]*1795+140, kps[frameCnt][num][14][1]*1167, mPaint);



        }catch (Exception exc){
            exc.printStackTrace();
        }finally {
            if(mCanvas != null){
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.e("EPlayerView","start create surface view...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(eView==null||eView.isNull()) return;
                draw(eView.getCurrentFrameCnt(), eView.getCurrentCamNum());
            }
        }).start();

        Log.e("EPlayerView","stop create surface view...");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
