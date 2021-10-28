package com.example.continuous_voice_recognition;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class Foreground extends Service {

    private static final String TAG = "ForegroundTag";

    // Notification
    private static final int NOTI_ID = 1;

    // STT
    private Intent intent;
    private SpeechRecognizer mRecognizer;

    public Foreground() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        createNotification();
        startSTT();
    }

    private void startSTT(){
        stopSTT();

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(intent);
    }

    private void stopSTT(){
        if (mRecognizer != null){
            mRecognizer.destroy();
            mRecognizer = null;
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            // 준비
        }

        @Override
        public void onBeginningOfSpeech() {
            // 시작
        }

        @Override
        public void onRmsChanged(float v) {
            // 입력받는 소리의 크기
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // 인식된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 중지
        }

        @Override
        public void onError(int i) {
// 네트워크 또는 인식 오류가 발생했을 때 호출
            String message;

            switch (i) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 에러";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "시간초과";
                    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }

            Log.d(TAG, "["+message+"] 에러 발생");
            startSTT();
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Toast.makeText(Foreground.this, matches.get(0), Toast.LENGTH_SHORT).show();

            startSTT();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            // 부분 인식 결과
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // 향후 이벤트 추가 예약
        }
    };

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("STT 변환");
        builder.setContentText("음성인식 중..");

        builder.setColor(Color.RED);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        builder.setContentIntent(pendingIntent); // 알림 클릭 시 이동

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        notificationManager.notify(NOTI_ID, builder.build()); // id : 정의해야하는 각 알림의 고유한 int값
        Notification notification = builder.build();
        startForeground(NOTI_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mRecognizer != null){
            mRecognizer.stopListening();
            mRecognizer.destroy();
            mRecognizer = null;
        }
    }
}