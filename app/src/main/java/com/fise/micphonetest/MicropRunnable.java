package com.fise.micphonetest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

public class MicropRunnable implements Runnable {
    // 是否录放的标记
    boolean isRecording = false;
    //static final int frequency = 44100;
    static final int frequency = 11025;

    @SuppressWarnings("deprecation")
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    // 定义录放音缓冲区大小
    int recBufSize, playBufSize;

    // 实例化录音对象
    AudioRecord audioRecord;

    // 实例化播放对象
    AudioTrack audioTrack;

    public MicropRunnable() {
        // 调用getMinBufferSize方法获得录音的最小缓冲空间
        recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);//add by chenqiang
        // 调用getMinBufferSize方法获得放音最小的缓冲区大小
        playBufSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);

        // 调用构造函数实例化录音对象
        int nMinBufSize = AudioRecord.getMinBufferSize(frequency, AudioFormat.CHANNEL_IN_MONO,
               AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, nMinBufSize);
        android.util.Log.d("chenqiang0105","=======get the audio now=========");
        // 调用构造函数实例化放音对象，以听筒模式播放
        //audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency, channelConfiguration, audioEncoding,
         //       playBufSize, AudioTrack.MODE_STREAM);

        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding,
                playBufSize, AudioTrack.MODE_STREAM);
        audioTrack.setPlaybackRate(frequency);//add by chenqiang
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[recBufSize];
            audioRecord.startRecording();// 开始录制
            audioTrack.play();// 开始播放
            android.util.Log.d("chenqiang0105","=======play=========");
            this.isRecording = true;
            while (isRecording) {
                // 从MIC保存数据到缓冲区
                int bufferReadResult = audioRecord.read(buffer, 0, recBufSize);

                byte[] tmpBuf = new byte[bufferReadResult];
                System.arraycopy(buffer, 0, tmpBuf, 0, bufferReadResult);

                // 写入数据就播放
                audioTrack.write(tmpBuf, 0, tmpBuf.length);
            }
            audioTrack.stop();
            audioRecord.stop();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // 停止方法
    public void no() {
        android.util.Log.d("chenqiang0105","=======stop=========");
        isRecording = false;
    }

    /*
    static final int bufferSize = 200000;
    final short[] buffer = new short[bufferSize];
    short[] readBuffer = new short[bufferSize];
    public void run() {
        isRecording = true;
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        int buffersize = AudioRecord.getMinBufferSize(11025, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 11025, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
        atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 11025, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
        atrack.setPlaybackRate(11025);
        byte[] buffer = new byte[buffersize];
        arec.startRecording();
        atrack.play();
        while(isRecording) {
            arec.read(buffer, 0, buffersize);
            atrack.write(buffer, 0, buffer.length);
        }
    }*/


}
