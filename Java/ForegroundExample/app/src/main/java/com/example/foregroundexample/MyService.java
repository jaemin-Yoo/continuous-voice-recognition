package com.example.foregroundexample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    private static final String TAG = "MyServiceTag";

    // Notification
    private static final int NOTI_ID = 1;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

@Override
public void onCreate() {
    super.onCreate();
    createNotification();
    mThread.start();
    Log.d(TAG, "onCreate");
}

@Override
public int onStartCommand(Intent intent, int flags, int startId) {

    Log.d(TAG, "onStartCommand");

    return super.onStartCommand(intent, flags, startId);
}

    private Thread mThread = new Thread("My Thread"){
        @Override
        public void run() {
            super.run();

            for (int i = 0; i<100; i++){
                Log.d(TAG, "count : "+i);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    };

    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Foreground Service");
        builder.setContentText("포그라운드 서비스");

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

        if (mThread != null){
            mThread.interrupt();
            mThread = null;
        }

        Log.d(TAG, "onDestroy");
    }
}