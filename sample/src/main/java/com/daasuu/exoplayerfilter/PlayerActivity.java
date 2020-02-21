package com.daasuu.exoplayerfilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daasuu.epf.EPlayerView;
import com.daasuu.epf.callbacks.QuardGridFilterCallback;
import com.dgene.mylibrary.NumberProgressBar;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerActivity extends AppCompatActivity implements QuardGridFilterCallback {

    //video view
    public EPlayerView ePlayerView;
    private SimpleExoPlayer player;
    private PlayerTimer playerTimer;
    private View rootView;

    //item view
    private SeekBar processBar;
    private ImageButton backBtn;
    private ImageButton startBtn;
    private ImageButton nextBtn;
    private ImageButton lockBtn;
    private ImageButton listBtn;
    private ImageButton angleBtn;
    private ImageButton leftBtn;
    private ImageButton rightBtn;
    private ImageButton boneBtn;
    private RelativeLayout angleLayout;
    private LinearLayout topLayout;
    private LinearLayout btmLayout;
    private RelativeLayout listLayout;
    private CalibrationView calibrationView;
    private ImageView upView;
    private ImageView downView;
    private ImageView centerView;
    private TextView videoName;
    private TextView lockStatus;
    private TextView angleText;
    private BoneSurfaceView boneSurfaceView;
    private ListView listView;

    //param
    private boolean lock = true;
    private boolean playStatus = true;
    private boolean fullscn_visiable = true;
    private boolean angle_visiable = true;
    private boolean bone_control = true;
    private boolean list_visiable = true;
    private int videoRows = 6;
    private int videoCols = 6;
    private String path = "http://106.75.100.102/api/videos";
    private List<ImageListArray> onePieceList = new ArrayList<>();
    private List<Map<String, String>> slist;
    private ImageListAdapter imageListAdapter;
    private pImg img = new pImg();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        init();

        //rootView=getWindow().getDecorView();
        //rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Intent intent=getIntent();  // 获取上一个活动传入的实例
        String data=intent.getStringExtra("send");  // 获取数据
        //data = "https://storage.3d.dgene.com/temp_multiViews/49779960b5a8e37d6f701ab70e2e19d5.mp4";

        if(data!=null) startPlay(data, videoRows, videoCols);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    void init(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);}
        processBar = (SeekBar) findViewById(R.id.progressbar);
        processBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (player == null) return;
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }
                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
                boneSurfaceView.ctrRender(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        boneSurfaceView.ctrRender(bone_control);
                    }
                }, 2000);
            }
        });
        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ePlayerView!=null) stopPlay();
                Intent intent = new Intent();
                intent.setClass(PlayerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        startBtn = findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) {
                    return;
                }

                if (playStatus) {
                    player.setPlayWhenReady(false);
                    startBtn.setBackgroundResource(R.drawable.play);
                } else {
                    player.setPlayWhenReady(true);
                    startBtn.setBackgroundResource(R.drawable.stop);
                }
                playStatus = !playStatus;
            }
        });
        nextBtn = findViewById(R.id.btn_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInvisiable();
            }
        });
        lockBtn = findViewById(R.id.btn_lock);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ePlayerView.slideLock();
                if(lock){
                    lockBtn.setBackgroundResource(R.drawable.lock);
                    lockStatus.setText(R.string.unlocked);
                }else{
                    lockBtn.setBackgroundResource(R.drawable.unlock);
                    lockStatus.setText(R.string.locked);
                }
                lock = !lock;
            }
        });
        boneBtn = findViewById(R.id.btn_bone);
        boneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bone_control = !bone_control;
                boneSurfaceView.ctrRender(bone_control);
            }
        });
        listBtn = findViewById(R.id.btn_list);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ePlayerView.isClicked = 2;

                new Thread() {//创建子线程进行网络访问的操作
                    public void run() {
                        try {
                            if(onePieceList.size()!=0) onePieceList.clear();
                            slist = getJSONObject(path);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                if(list_visiable){
                    setInvisiable();
                    listLayout.setVisibility(View.VISIBLE);
                    listLayout.setAlpha(1);
                }
                list_visiable = !list_visiable;
            }
        });
        angleBtn = findViewById(R.id.btn_angle);
        angleLayout = findViewById(R.id.angle_layout);
        angleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ePlayerView.isClicked = 2;
                angleLayout.setAlpha(1);
                angle_visiable = !angle_visiable;
                if(!angle_visiable){
                    setInvisiable();
                    angleLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        calibrationView = findViewById(R.id.calibration_view);
        calibrationView.setNotSize(5);
        calibrationView.setCircleSize(5);
        upView = findViewById(R.id.shadow_up);
        downView = findViewById(R.id.shadow_down);
        topLayout = findViewById(R.id.top_layout);
        btmLayout = findViewById(R.id.bottom_layout);
        centerView = findViewById(R.id.img_center);
        centerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angleLayout.setVisibility(View.INVISIBLE);
                angle_visiable = !angle_visiable;
                setVisiable();
            }
        });
        leftBtn = findViewById(R.id.angle_left);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationView.setCurrentPosition(calibrationView.getCurrentPosition()==5?1:calibrationView.getCurrentPosition()+1);
                ePlayerView.changeViewIndex(calibrationView.getCurrentPosition());
                angleText.setText("视频视角："+(6-calibrationView.getCurrentPosition()));
            }
        });
        rightBtn = findViewById(R.id.angle_right);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrationView.setCurrentPosition(calibrationView.getCurrentPosition()==1?5:calibrationView.getCurrentPosition()-1);
                ePlayerView.changeViewIndex(calibrationView.getCurrentPosition());
                angleText.setText("视频视角："+(6-calibrationView.getCurrentPosition()));
            }
        });
        videoName = findViewById(R.id.video_name);
        lockStatus = findViewById(R.id.lock_status);
        angleText = findViewById(R.id.angle_txt);
        boneSurfaceView = findViewById(R.id.action_preview);
        listLayout = findViewById(R.id.list_layout);
        imageListAdapter = new ImageListAdapter(PlayerActivity.this, R.layout.listitem, onePieceList);
        listView = (ListView)findViewById(R.id.video_list); //将适配器导入Listview
        listView.setAdapter(imageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Map<String, String> selectedItem = slist.get(i);
                stopPlay();
                startPlay(selectedItem.get("weburl"),Integer.parseInt(selectedItem.get("rows")),Integer.parseInt(selectedItem.get("cols")));
                ePlayerView.isClicked = 2;
                ePlayerView.isLocked = lock;
                videoName.setText(selectedItem.get("name"));
                Log.e("jsonHandler",selectedItem.get("weburl"));
            }
        });
    }



    void setInvisiable(){
        upView.setVisibility(View.INVISIBLE);
        downView.setVisibility(View.INVISIBLE);
        topLayout.setVisibility(View.INVISIBLE);
        btmLayout.setVisibility(View.INVISIBLE);
        topLayout.setAlpha(0); btmLayout.setAlpha(0);
        upView.setImageAlpha(0); downView.setImageAlpha(0);
        //rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    void setVisiable(){
        upView.setVisibility(View.VISIBLE);
        downView.setVisibility(View.VISIBLE);
        topLayout.setVisibility(View.VISIBLE);
        btmLayout.setVisibility(View.VISIBLE);
        topLayout.setAlpha(1); btmLayout.setAlpha(1);
        upView.setImageAlpha(255); downView.setImageAlpha(255);
    }

    private void startPlay(String videoUrl, int rows, int cols){
        setUpSimpleExoPlayer(videoUrl);
        setUoGlPlayerView(rows, cols);
        setUpTimer();
    }

    private void setUpSimpleExoPlayer(String videoUrl) {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "hls"));
        int type = videoUrl.indexOf("m3u8");
        if (type != -1) {
            HlsMediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl));        // SimpleExoPlayer

            player = ExoPlayerFactory.newSimpleInstance(this);
            // Prepare the player with the source.
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        }
        else{
            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl));        // SimpleExoPlayer
            player = ExoPlayerFactory.newSimpleInstance(this);
            // Prepare the player with the source.
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        }
    }
    private void setUoGlPlayerView(int rows, int cols) {
        ePlayerView = new EPlayerView(this, rows, cols, this);
        ePlayerView.setSimpleExoPlayer(player);
        ePlayerView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).addView(ePlayerView);
        ePlayerView.initAll(topLayout, btmLayout, upView, downView, angleLayout, listLayout);
        calibrationView.setView(ePlayerView,angleText);
        ePlayerView.onResume();
    }


    private void setUpTimer() {
        playerTimer = new PlayerTimer();
        playerTimer.setCallback(new PlayerTimer.Callback() {
            @Override
            public void onTick(long timeMillis) {
                long position = player.getCurrentPosition();
                long duration = player.getDuration();

                if (duration <= 0) return;

                processBar.setMax((int) duration / 1000);
                processBar.setProgress((int) position / 1000);
            }
        });
        playerTimer.start();
    }

    private void releasePlayer() {
        if(ePlayerView!=null){
            ePlayerView.onPause();
        }

        ((MovieWrapperView) findViewById(R.id.layout_movie_wrapper)).removeAllViews();

        if(player!=null){
            player.stop();
            player.release();
            player = null;
        }

    }

    private void stopPlay(){
        releasePlayer();
        if (playerTimer != null) {
            playerTimer.stop();
            playerTimer.removeMessages(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay();
    }

    @Override
    public int getNewViewIndex() {
        return 0;
    }


    private class pImg{
        private List<Bitmap> mapList = new ArrayList<>();
        //pImg(){}
        public Bitmap getImage(int i) {
            return mapList.get(i);
        }
        public void addImg(Bitmap bitmap){
            mapList.add(bitmap);
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    for(int i=0; i<slist.size();i++){
                        Map<String, String> map = slist.get(i); // 例子而已，直接获取下标为2的值了，可以通过循环将list的值取出
                        Bitmap newMap = img.getImage(i);
                        ImageListArray ace =new ImageListArray(map.get("name"),newMap);
                        onePieceList.add(ace);
                    }

                    imageListAdapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 获取网络中的JSON数据
     * @param path
     * @return
    //* @throws Exception
     */
    public List<Map<String, String>> getJSONObject(String path)
            throws Exception {

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        URL url = new URL(path);
        // 利用HttpURLConnection对象，我们可以从网页中获取网页数据
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 单位为毫秒，设置超时时间为5秒
        conn.setConnectTimeout(15 * 1000);
        // HttpURLConnection对象是通过HTTP协议请求path路径的，所以需要设置请求方式，可以不设置，因为默认为get
        conn.setRequestMethod("GET");


        if (conn.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            String json = new String(data); // 把字符串组转换成字符串

            // 数据形式：{"total":2,"success":true,"arrayData":[{"id":1,"name":"张三"},{"id":2,"name":"李斯"}]}
            JSONObject jsonObject = new JSONObject(json); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            int status = jsonObject.getInt("status");

            // 里面有一个数组数据，可以用getJSONArray获取数组
            JSONArray jsonArray = jsonObject.getJSONArray("data");


            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); // 得到每个对象
                String createTime = item.getString("create_time");
                String desc = item.getString("description");
                String duration = item.getString("duration");
                String name = item.getString("name");
                String thumbnail = item.getString("thumbnail");
                String rows = item.getString("rows");
                String cols = item.getString("cols");
                String weburl = item.getString("url");
                int videoId = item.getInt("video_id");



                map = new HashMap<String, String>();
                map.put("createTime", createTime);
                map.put("desc", desc);
                map.put("duration", duration);
                map.put("name", name);
                map.put("thumbnail", thumbnail);
                map.put("weburl", weburl);
                map.put("videoId", videoId + "");
                map.put("rows", rows);
                map.put("cols", cols);
                list.add(map);

                long startTime1 = System.currentTimeMillis();
                Bitmap bp = getURLimage(thumbnail);
                long endTime1 = System.currentTimeMillis();
                System.out.println("runtime: " + (endTime1 - startTime1) + "ms");


                img.addImg(bp);
            }

        }

        return list;
    }

    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }

    private static Bitmap getURLimage(String imageUrl) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(imageUrl);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(600);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();


            //writeToFile(ByteBuffer.wrap(readStream(is)),"recv.jpg");

            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;

    }

}
