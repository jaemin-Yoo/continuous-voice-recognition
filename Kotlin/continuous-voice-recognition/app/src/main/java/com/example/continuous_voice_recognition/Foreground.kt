package com.example.continuous_voice_recognition

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class Foreground : Service() {
    // STT
    private var intent: Intent? = null
    private var mRecognizer: SpeechRecognizer? = null
    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()

        // RecognizerIntent 생성
        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        createNotification()
        startSTT()
    }

    private fun startSTT() {
        stopSTT()
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        mRecognizer!!.setRecognitionListener(listener)
        mRecognizer!!.startListening(intent)
    }

    private fun stopSTT() {
        if (mRecognizer != null) {
            mRecognizer!!.destroy()
            mRecognizer = null
        }
    }

    private val listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(bundle: Bundle) {
            // 준비
        }

        override fun onBeginningOfSpeech() {
            // 시작
        }

        override fun onRmsChanged(v: Float) {
            // 입력받는 소리의 크기
        }

        override fun onBufferReceived(bytes: ByteArray) {
            // 인식된 단어를 buffer에 담음
        }

        override fun onEndOfSpeech() {
            // 중지
        }

        override fun onError(i: Int) {
// 네트워크 또는 인식 오류가 발생했을 때 호출
            val message: String
            message = when (i) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버 에러"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "시간초과"
                else -> "알 수 없는 오류"
            }
            Log.d(TAG, "[$message] 에러 발생")
            startSTT()
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            Toast.makeText(this@Foreground, matches!![0], Toast.LENGTH_SHORT).show()
            startSTT()
        }

        override fun onPartialResults(bundle: Bundle) {
            // 부분 인식 결과
        }

        override fun onEvent(i: Int, bundle: Bundle) {
            // 향후 이벤트 추가 예약
        }
    }

    private fun createNotification() {
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle("STT 변환")
        builder.setContentText("음성인식 중..")
        builder.color = Color.RED
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent) // 알림 클릭 시 이동

        // 알림 표시
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        notificationManager.notify(NOTI_ID, builder.build()) // id : 정의해야하는 각 알림의 고유한 int값
        val notification = builder.build()
        startForeground(NOTI_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRecognizer != null) {
            mRecognizer!!.stopListening()
            mRecognizer!!.destroy()
            mRecognizer = null
        }
    }

    companion object {
        private const val TAG = "ForegroundTag"

        // Notification
        private const val NOTI_ID = 1
    }
}