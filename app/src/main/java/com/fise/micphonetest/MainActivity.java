package com.fise.micphonetest;

import com.fise.micphonetest.MicropRunnable;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.view.WindowManager;
import android.os.SystemClock;
import android.widget.Chronometer;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements
        OnClickListener {
    MicropRunnable run_Metned = new MicropRunnable();
    private AudioManager audioManager;
    Thread thread = null;
    private EditText timelength;
    Button playbutton, stopbutton;
    private Timer timer;
    private Chronometer time;
    private long recordingTime = 0;

    public void onRecordStart()// time start
    {
        time.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
        time.start();
    }

    public void onRecordPause()// time pause
    {
        time.stop();
        recordingTime = SystemClock.elapsedRealtime() - time.getBase();// 保存这次记录了的时间
    }

    public void onRecordStop()// time stop
    {
        recordingTime = 0;
        time.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        timelength = (EditText) this.findViewById(R.id.timelength);
        timelength.setText("0");
        time = (Chronometer) this.findViewById(R.id.chronometer);//引入计时器控件
        time.setBase(SystemClock.elapsedRealtime());//初始化时间
        // 按键监听
        playbutton = (Button) findViewById(R.id.playbutton);
        stopbutton = (Button) findViewById(R.id.stopbutton);
        playbutton.setOnClickListener(this);
        stopbutton.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playbutton:
                openSpeaker();
                // 按钮【开】
                long t = Long.parseLong("" + timelength.getText());//以小时为单位设置时间
                if (t != 0) {

                    Thread thread = new Thread(run_Metned);//将麦克风开启方法加入线程体中
                    android.util.Log.d("chenqiang0105","play button is now");
                    thread.start();
                    onRecordStart();
                    long s = 1000 * 3600 * t ;//将小时数转化为毫秒数
                    timer = new Timer(true);

                    TimerTask task = new TimerTask()// 计时任务设置
                    {
                        public void run() {
                            run_Metned.no();
                            onRecordPause();
                            timer.cancel();

                        }
                    };
                    timer.schedule(task, s, 1);//s毫秒后停止
                } else if (t == 0) {
                    run_Metned.no();
                } else if (timelength.getText() == null) {
                    run_Metned.no();
                    timelength.setText("0");
                }
                break;
            case R.id.stopbutton:
                // 按钮【关】
                android.util.Log.d("chenqiang0105","stopbutton is now");
                run_Metned.no();
                timer.cancel();
                onRecordStop();
                onRecordPause();
                timelength.setText("0");
                break;
        }
    }

    int i = 0;

    public boolean onKeyDown(int keyCode, KeyEvent event) //双击返回键退出
    {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            long t = Long.parseLong("" + timelength.getText());
            i++;
            if (i == 2 && t != 0) {
                run_Metned.no();
                timer.cancel();
                onRecordStop();
                onRecordPause();
            }
            if (i == 3) {
                finish();
            }

        }
        return true;
    }

    public void openSpeaker() {
        try {
            android.util.Log.d("chenqiang0105","=======openSpeaker=========");
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                           audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                          AudioManager.STREAM_MUSIC);
            //audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            android.util.Log.d("chenqiang0105","=======openSpeaker22222222222=========");
            // 判断扬声器是否在打开
            //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            // 获取当前通话音量
            int currVolume = 0;
            //currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            //currVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            //if (!audioManager.isSpeakerphoneOn()) {
                //audioManager.setSpeakerphoneOn(true);
                android.util.Log.d("chenqiang0105","=======setSpeakerphoneOn=========");
                //audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                //        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                //        AudioManager.STREAM_VOICE_CALL);

                //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                 //       audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                 //       AudioManager.STREAM_MUSIC);

                setVolumeControlStream(AudioManager.STREAM_MUSIC);
                //audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setMode(AudioManager.MODE_NORMAL);

            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

