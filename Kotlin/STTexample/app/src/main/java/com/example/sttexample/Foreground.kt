package com.example.sttexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class Foreground : Service() {

    val CHANNEL_ID = "FGS1"
    val NOTI_ID = 1

    private var speechRecognizer: SpeechRecognizer? = null
    private val TAG = "STT_Log"

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startSTT()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("STT service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .build()

        startForeground(NOTI_ID, notification)

        return super.onStartCommand(intent, flags, startId)
    }

    private  fun startSTT() {
        stopSTT()

        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }
    }

    private  fun stopSTT() {
        if (speechRecognizer != null) {
            speechRecognizer!!.destroy()
            speechRecognizer = null
        }
    }

    private fun recognitionListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "음성인식 대기")
        }

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "음성인식 완료")
        }

        override fun onError(error: Int) {
            var message = ""

            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "퍼미션 없음"
                SpeechRecognizer.ERROR_AUDIO -> message = "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> message = "클라이언트 에러"
                SpeechRecognizer.ERROR_NETWORK -> message = "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> message = "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RECOGNIZER이 바쁨"
                SpeechRecognizer.ERROR_SERVER -> message = "서버 에러"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "시간 초과"
            }

            Log.d(TAG, "["+message+"] 에러 발생")
            startSTT()
        }

        override fun onResults(results: Bundle) {
            var result = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]

            Log.d(TAG, "인식된 음성: "+result)
            Toast.makeText(this@Foreground,result,Toast.LENGTH_SHORT).show()

            startSTT()
        }
    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val serviceChannel = NotificationChannel(CHANNEL_ID, "FOREGROUND", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {

        if (speechRecognizer != null) {
            speechRecognizer!!.stopListening()
            speechRecognizer!!.destroy()
            speechRecognizer = null
        }

        super.onDestroy()
    }



    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }
}