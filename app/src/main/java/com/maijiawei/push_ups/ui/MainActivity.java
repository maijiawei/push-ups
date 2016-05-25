package com.maijiawei.push_ups.ui;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.maijiawei.push_ups.ui.dialog.CompleteDialog;
import com.maijiawei.push_ups.model.HistoryModel;
import com.maijiawei.push_ups.util.PreferenceUtils;
import com.maijiawei.push_ups.R;
import com.umeng.update.UmengUpdateAgent;

import org.w3c.dom.Text;

public class MainActivity extends BaseActivity implements View.OnClickListener, SensorEventListener {

    public static final String KEY_SETTING_VOICE = "voice";
    View mWrapMain,mWrapPushUps,mWrapCountdown;
    Button mBtnTrain,mBtnFree,mBtnStop,mBtnSkip;
    ImageButton mImgBtnHistory,mImgBtnAbout,mImgBtnVoice;
    TextView mTvCountdownTimer,mTvPushUpsTimer,mTvPushUpsCounter,mTvCountdownText;
    TextView mTvGroup_1,mTvGroup_2,mTvGroup_3,mTvGroup_4,mTvGroup_5;
    ArrayList<TextView> mGroupViews = new ArrayList<>();

    PowerManager mPowerManager;
    PowerManager.WakeLock mWakeLock;
    SensorManager mSensorManager;
    MediaPlayer mMediaPlayer;
    int mPlayingResId = 0;
    ArrayList<Integer> mPlayResids = new ArrayList<>();
    String[] mGroupResStr = {"first_group","second_group","third_group","forth_group","last_group"};

    Timer mCountdownTimer;
    Timer mPushUpsTimer;

    int mPushUpsCount = 0;//俯卧撑计数
    int mPushUpsGroupCount = 0;//俯卧撑组计数
    int mPushUpsTimeCount = 0;//俯卧撑时间计数
    float mFirstVal = 0;//传感器首次记录数据
    boolean mIsNoted = false;//是否已经记录传感器首次数据
    boolean mIsRun = false;
    boolean mIsActive = false;
    boolean mIsTrain = false;//false is free
    int mTrainWhich = 0;

    int[][] mTrains ={
            {2,2,2,2,3},
            {6,6,5,4,5},
            {9,9,8,6,7},
            {4,3,2,2,4},
            {8,8,6,5,7},
            {11,11,9,9,10},
            {5,4,4,3,5},
            {9,8,8,5,9},
            {14,12,10,10,14},
            {4,5,4,4,5},
            {8,7,5,4,6},
            {11,11,6,5,9},
            {6,5,3,4,6},
            {10,8,6,7,9},
            {12,12,10,10,12},
            {5,6,4,5,6},
            {9,9,7,7,9},
            {14,14,11,11,10},
            {9,8,10,8,10},
            {14,10,9,11,8},
            {16,14,12,11,13},
            {13,10,11,11,10},
            {15,15,13,13,10},
            {18,15,14,13,17},
            {15,11,14,10,11},
            {20,14,14,12,18},
            {21,15,16,14,20},
            {14,11,11,9,13},
            {14,12,14,12,15},
            {20,16,18,15,22},
            {14,12,12,10,13},
            {20,12,14,13,16},
            {22,18,17,16,23},
            {18,11,13,12,13},
            {20,17,14,15,19},
            {25,19,18,18,24},
            {21,18,14,13,19},
            {22,21,16,20,22},
            {26,22,18,21,25},
            {18,11,13,12,13},
            {13,12,10,8,28},
            {15,14,12,10,32},
            {14,11,12,9,13},
            {10,10,8,7,28},
            {14,12,10,8,33},
            {25,21,20,18,25},
            {29,25,21,20,30},
            {34,26,24,21,32},
            {13,12,10,8,28},
            {15,14,12,10,33},
            {16,14,11,10,36},
            {10,10,8,7,28},
            {14,12,10,8,33},
            {14,12,10,8,36},
            {30,21,26,18,25},
            {34,25,21,25,35},
            {41,26,33,25,35},
            {25,14,10,8,23},
            {13,14,11,10,33},
            {20,18,11,10,28},
            {19,12,10,8,28},
            {14,12,10,8,30},
            {14,12,10,18,36},
            {36,28,25,24,33},
            {46,36,32,34,46},
            {52,41,38,36,52},
            {18,16,13,11,38},
            {21,18,18,14,46},
            {26,21,21,18,52},
            {20,17,15,14,42},
            {23,20,20,16,50},
            {27,23,23,20,56},
    };
    String[] mTrainsText = {
            "级别1-1(简单)        2-2-2-2-3",
            "级别1-1(一般)        6-6-5-4-5",
            "级别1-1(困难)        9-9-8-6-7",
            "级别1-2(简单)        4-3-2-2-4",
            "级别1-2(一般)        8-8-6-5-7",
            "级别1-2(困难)        11-11-9-9-10",
            "级别1-3(简单)        5-4-4-3-5",
            "级别1-3(一般)        9-8-8-5-9",
            "级别1-3(困难)        14-12-10-10-14",
            "级别2-1(简单)        4-5-4-4-5",
            "级别2-1(一般)        8-7-5-4-6",
            "级别2-1(困难)        11-11-6-5-9 ",
            "级别2-2(简单)        6-5-3-4-6",
            "级别2-2(一般)        10-8-6-7-9",
            "级别2-3(困难)        12-12-10-10-12",
            "级别2-3(简单)        5-6-4-5-6",
            "级别2-3(一般)        9-9-7-7-9",
            "级别2-3(困难)        14-14-11-11-10",
            "级别3-1(简单)        9-8-10-8-10 ",
            "级别3-1(一般)        14-10-9-11-8",
            "级别3-1(困难)        16-14-12-11-13",
            "级别3-2(简单)        13-10-11-11-10",
            "级别3-2(一般)        15-15-13-13-10",
            "级别3-2(困难)        18-15-14-13-17",
            "级别3-3(简单)        15-11-14-10-11",
            "级别3-3(一般)        20-14-14-12-18",
            "级别3-3(困难)        21-15-16-14-20",
            "级别4-1(简单)        14-11-11-9-13",
            "级别4-1(一般)        14-12-14-12-15",
            "级别4-1(困难)        20-16-18-15-22",
            "级别4-2(简单)        14-12-12-10-13",
            "级别4-2(一般)        20-12-14-13-16",
            "级别4-2(困难)        22-18-17-16-23",
            "级别4-3(简单)        18-11-13-12-13",
            "级别4-3(一般)        20-17-14-15-19",
            "级别4-3(困难)        25-19-18-18-24",
            "级别5-1(简单)        21-18-14-13-19",
            "级别5-1(一般)        22-21-16-20-22",
            "级别5-1(困难)        26-22-18-21-25",
            "级别5-2(简单)        18-11-13-12-13",
            "级别5-2(一般)        13-12-10-8-28",
            "级别5-2(困难)        15-14-12-10-32",
            "级别5-3(简单)        14-11-12-9-13",
            "级别5-3(一般)        10-10-8-7-28",
            "级别5-3(困难)        14-12-10-8-33",
            "级别6-1(简单)        25-21-20-18-25",
            "级别6-1(一般)        29-25-21-20-30",
            "级别6-1(困难)        34-26-24-21-32",
            "级别6-2(简单)        13-12-10-8-28",
            "级别6-2(一般)        15-14-12-10-33",
            "级别6-2(困难)        16-14-11-10-36",
            "级别6-3(简单)        10-10-8-7-28",
            "级别6-3(一般)        14-12-10-8-33",
            "级别6-3(困难)        14-12-10-8-36",
            "级别7-1(简单)        30-21-26-18-25",
            "级别7-1(一般)        34-25-21-25-35",
            "级别7-1(困难)        41-26-33-25-35",
            "级别7-2(简单)        25-14-10-8-23",
            "级别7-2(一般)        13-14-11-10-33",
            "级别7-3(困难)        20-18-11-10-28",
            "级别7-3(简单)        19-12-10-8-28",
            "级别7-3(一般)        14-12-10-8-30",
            "级别7-3(困难)        14-12-10-18-36",
            "级别8-1(简单)        36-28-25-24-33",
            "级别8-1(一般)        46-36-32-34-46",
            "级别8-1(困难)        52-41-38-36-52",
            "级别8-2(简单)        18-16-13-11-38",
            "级别8-2(一般)        21-18-18-14-46",
            "级别8-3(困难)        26-21-21-18-52",
            "级别8-3(简单)        20-17-15-14-42",
            "级别8-3(一般)        23-20-20-16-50",
            "级别8-3(困难)        27-23-23-20-56",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init view
        initViews();
        //申请屏幕常亮
        mPowerManager = (PowerManager) getSystemService(Service.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Lock");
        mWakeLock.setReferenceCounted(false);

        //距离感应器
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        UmengUpdateAgent.update(this);

    }

    @Override
    protected void onResume() {
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        mIsActive = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        mWakeLock.release();//撤销屏幕常亮
        mIsActive = false;
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.history:
                Intent intent = new Intent(this, HistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent2 = new Intent(this, AboutActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.train:
                train();
                break;
            case R.id.free:
                free();
                break;
            case R.id.push_ups_stop:
                if(mIsTrain){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("你确定要放弃训练吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    stop();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }else{
                    if(mIsTrain){
                        stop();
                    }else{
                        addPlayByStr("well_done");//完成训练
                        playMedia();
                        final ArrayList<Integer> numbers = new ArrayList<>();
                        numbers.add(mPushUpsCount);
                        mIsRun = false;
                        new CompleteDialog()
                                .setNumbers(numbers)
                                .setTime(mPushUpsTimeCount)
                                .setListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        save(numbers,mPushUpsTimeCount);
                                        stop();
                                    }
                                }).show(getSupportFragmentManager(),"CompleteDialog");
                    }
                }
                break;
            case R.id.voice:
                if(PreferenceUtils.getBoolean(this, KEY_SETTING_VOICE, true)){
                    PreferenceUtils.setBoolean(this,KEY_SETTING_VOICE,false);
                }else{
                    PreferenceUtils.setBoolean(this,KEY_SETTING_VOICE,true);
                }
                initSettingViews();
                break;
        }
    }

    void save(ArrayList<Integer> numbers,int time){
        String val = "";
        for (int i=0;i<numbers.size();i++){
            if((i+1) == numbers.size()){
                val += numbers.get(i);
            }else{
                val += numbers.get(i) + "-";
            }
        }
        HistoryModel history = new HistoryModel();
        history.time = time;
        history.val = val;
        history.createTime = System.currentTimeMillis();
        history.save();

        Log.i("test",HistoryModel.getAll().get(0).val+"");
    }

    void free(){
        mIsTrain = false;
        start();
    }

    void train(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(mTrainsText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mIsTrain = true;
                mTrainWhich = which;
                start();
            }
        });
        builder.show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float val = event.values[0];

        if(!mIsNoted){
            mIsNoted = true;
            mFirstVal = val;
        }
        if(mFirstVal != val  && mIsRun && mIsActive){
            onPushUps();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    void onPushUps(){
        mPushUpsCount++;
        //训练模式
        if(mIsTrain){
            onTrain();
        }else{
            onFree();
        }

        mTvPushUpsCounter.setText(String.valueOf(mPushUpsCount));

        //playMedia();//开始播放

    }

    void startFree(){
        addPlayByStr("go");
        onFree();
    }
    void onFree() {
        mIsRun = true;
        if(mPushUpsCount > 0){
            addPlayByNum(mPushUpsCount);//添加计数声音
        }
        playMedia();//开始播放
    }

    /**
     * 训练模式
     */
    void startTrain() {
        int groupLength = mTrains[mTrainWhich].length;//组数量
        if((mPushUpsGroupCount+1) == groupLength) {
            addPlayByStr("last_group");
        }else{
            addPlayByStr(mGroupResStr[mPushUpsGroupCount]);
        }
        addPlayByStr("go");
        onTrain();
    }
    void onTrain() {
        int groupLength = mTrains[mTrainWhich].length;//组数量
        int val = mTrains[mTrainWhich][mPushUpsGroupCount];//当前组有多少个俯卧撑
        mIsRun = true;
        if(mPushUpsCount > 0){
            addPlayByNum(mPushUpsCount);//添加计数声音
        }

        if(mPushUpsCount == val){//完成当前组
            mPushUpsGroupCount++;//组+1
            mPushUpsCount = 0;//俯卧撑数重置为0

            mIsRun = false;
            if(mPushUpsGroupCount == groupLength) {
                addPlayByStr("well_done");//完成训练
                playMedia();
                final ArrayList<Integer> numbers = new ArrayList<>();
                for (int i=0;i<mTrains[mTrainWhich].length;i++){
                    numbers.add(mTrains[mTrainWhich][i]);
                }
                new CompleteDialog()
                        .setNumbers(numbers)
                        .setTime(mPushUpsTimeCount)
                        .setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save(numbers,mPushUpsTimeCount);
                                stop();
                            }
                        }).show(getSupportFragmentManager(),"CompleteDialog");
                return;
            }
            addPlayByStr("take_a_rest");//休息一下吧
            showCountdown("休息一下吧",true,30, false, new OnCountdownListener() {
                @Override
                public void over() {
                    addPlayByStr("rest_end");//休息结束
                    startTrain();
                }
            });
            playMedia();
            return;
        }

        int currentItemBgId = R.drawable.group_middle_current_item_bg;
        if(mPushUpsGroupCount == 0){
            currentItemBgId = R.drawable.group_left_current_item_bg;
        }else if((mPushUpsGroupCount+1) == groupLength){
            currentItemBgId = R.drawable.group_right_current_item_bg;
        }
        mGroupViews.get(mPushUpsGroupCount).setBackgroundResource(currentItemBgId);

        for(int i=0;i < mPushUpsGroupCount;i++){
            int completeItemBgId = R.drawable.group_middle_complete_item_bg;
            if(i == 0){
                completeItemBgId = R.drawable.group_left_complete_item_bg;
            }else if((i+1) == groupLength){
                completeItemBgId = R.drawable.group_right_complete_item_bg;
            }
            mGroupViews.get(i).setBackgroundResource(completeItemBgId);
        }

        playMedia();
    }

    void initViews() {

        //wrap
        mWrapMain = findViewById(R.id.main_wrap);
        mWrapPushUps = findViewById(R.id.push_ups_wrap);
        mWrapCountdown = findViewById(R.id.countdown_wrap);

        //button
        mBtnTrain = (Button) findViewById(R.id.train);
        mBtnFree = (Button) findViewById(R.id.free);
        mBtnStop = (Button) findViewById(R.id.push_ups_stop);
        mBtnSkip = (Button) findViewById(R.id.skip);

        //imageButtun
        mImgBtnHistory = (ImageButton) findViewById(R.id.history);
        mImgBtnAbout = (ImageButton) findViewById(R.id.about);
        mImgBtnVoice = (ImageButton) findViewById(R.id.voice);

        //textview
        mTvCountdownTimer = (TextView) findViewById(R.id.countdown_timer);
        mTvPushUpsTimer = (TextView) findViewById(R.id.push_ups_timer);
        mTvPushUpsCounter = (TextView) findViewById(R.id.push_ups_counter);
        mTvCountdownText = (TextView) findViewById(R.id.countdown_text);

        mTvGroup_1 = (TextView) findViewById(R.id.group_1);
        mTvGroup_2 = (TextView) findViewById(R.id.group_2);
        mTvGroup_3 = (TextView) findViewById(R.id.group_3);
        mTvGroup_4 = (TextView) findViewById(R.id.group_4);
        mTvGroup_5 = (TextView) findViewById(R.id.group_5);

        mGroupViews.add(mTvGroup_1);
        mGroupViews.add(mTvGroup_2);
        mGroupViews.add(mTvGroup_3);
        mGroupViews.add(mTvGroup_4);
        mGroupViews.add(mTvGroup_5);

        //bind event
        mBtnTrain.setOnClickListener(this);
        mBtnFree.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnSkip.setOnClickListener(this);
        mImgBtnHistory.setOnClickListener(this);
        mImgBtnAbout.setOnClickListener(this);
        mImgBtnVoice.setOnClickListener(this);

        initSettingViews();
    }

    void initSettingViews(){

        boolean voice = PreferenceUtils.getBoolean(this, KEY_SETTING_VOICE, true);//true is sound,false is silent
        if(voice){
            mImgBtnVoice.setBackgroundResource(R.drawable.title_bar_sound_bg);
        }else{
            mImgBtnVoice.setBackgroundResource(R.drawable.title_bar_silent_bg);
        }

    }

    void start() {
        showCountdown("准备开始",false,3, true, new OnCountdownListener() {
            @Override
            public void over() {
                pushUps();
            }
        });

    }

    /**
     * 显示倒计时
     * @param second
     * @param isSound
     * @param listener
     */
    void showCountdown(String msg, boolean isShowBtn,final int second, final boolean isSound, final OnCountdownListener listener) {
        if(isShowBtn){
            mBtnSkip.setVisibility(View.VISIBLE);
        }else{
            mBtnSkip.setVisibility(View.GONE);
        }
        mTvCountdownText.setText(msg);
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCountdownTimer != null){
                    showWrap(mWrapPushUps);
                    mCountdownTimer.cancel();
                    mCountdownTimer = null;
                }
                listener.over();
            }
        });
        final Handler handler = new Handler(){
            int count = second;
            @Override
            public void handleMessage(Message msg) {
                if(count == second){
                    showWrap(mWrapCountdown);
                }else if(count == 0){
                    showWrap(mWrapPushUps);
                    mCountdownTimer.cancel();
                    mCountdownTimer = null;
                    listener.over();
                    return;
                }

                mTvCountdownTimer.setText(String.valueOf(count));

                if(isSound){
                    addPlayByNum(count);
                    playMedia();
                }

                count--;
                super.handleMessage(msg);
            }
        };

        //开始倒计时
        mCountdownTimer = new Timer();
        mCountdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, 1000);
    }

    public interface OnCountdownListener {
        void over();
    }

    void showWrap(View view) {
        mWrapMain.setVisibility(View.GONE);
        mWrapCountdown.setVisibility(View.GONE);
        mWrapPushUps.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    void addPlayByNum(int num) {

        if(num >= 160){
            addPlayByStr("fight");
            return;
        }

        String rsIdStr = "n";
        if(num < 10){
            rsIdStr += "00" + String.valueOf(num);
        }else if(num < 100){
            rsIdStr += "0" + String.valueOf(num);
        }else{
            rsIdStr += String.valueOf(num);
        }
        addPlayByStr(rsIdStr);
    }

    void addPlayByStr(String str) {
        boolean voice = PreferenceUtils.getBoolean(this, KEY_SETTING_VOICE, true);
        if(!voice){
            return;
        }
        Resources res=getResources();
        int rsId = res.getIdentifier(str, "raw", getPackageName());
        mPlayResids.add(rsId);
    }

    void playMedia() {

        if(mPlayResids.size() > 0){
            if(mPlayingResId == mPlayResids.get(0) || !mIsActive){
                mMediaPlayer.release();
                mPlayResids = new ArrayList<>();
                playMedia();
                return;
            }
            mPlayingResId = mPlayResids.get(0);
            mMediaPlayer = MediaPlayer.create(this,mPlayingResId);
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {//播出完毕事件
                @Override public void onCompletion(MediaPlayer arg0) {
                    mMediaPlayer.release();
                    if(mPlayResids.size() > 0){
                        mPlayResids.remove(0);
                    }
                    mPlayingResId = 0;
                    playMedia();//顺序播放
                }
            });
        }

    }

    void pushUps(){

        //初始化
        for (int i=0;i<mGroupViews.size();i++){
            mGroupViews.get(i).setVisibility(View.GONE);
            mGroupViews.get(i).setBackgroundColor(Color.TRANSPARENT);
        }
        mPushUpsTimeCount = mPushUpsCount = mPushUpsGroupCount = 0;
        mTvPushUpsCounter.setText(String.valueOf(mPushUpsCount));
        mWakeLock.acquire();//屏幕常亮

        //训练模式
        if(mIsTrain){
            //显示组views
            for (int i = 0;i<mTrains[mTrainWhich].length;i++){
                String val = String.valueOf(mTrains[mTrainWhich][i]);
                mGroupViews.get(i).setText(val);
                mGroupViews.get(i).setVisibility(View.VISIBLE);
            }
            startTrain();
        }else{
            startFree();
        }

        //时间
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(!mIsRun || !mIsActive){
                    return;
                }
                int minutesInt = mPushUpsTimeCount / 60;
                int secondsInt = mPushUpsTimeCount % 60;
                String minutesStr = minutesInt < 10 ? "0"+minutesInt : String.valueOf(minutesInt);
                String secondsStr = secondsInt < 10 ? "0"+secondsInt : String.valueOf(secondsInt);
                String timerStr = minutesStr + ":" + secondsStr;
                mTvPushUpsTimer.setText(timerStr);

                mPushUpsTimeCount++;
                super.handleMessage(msg);
            }
        };

        mPushUpsTimer = new Timer();
        mPushUpsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        },0,1000);

    }

    void stop() {

        mIsRun = false;
        mWakeLock.release();//撤销屏幕常亮

        if(mCountdownTimer != null){
            mCountdownTimer.cancel();
            mCountdownTimer = null;
        }
        if(mPushUpsTimer != null){
            mPushUpsTimer.cancel();
            mPushUpsTimer = null;
        }

        showWrap(mWrapMain);
    }

}
