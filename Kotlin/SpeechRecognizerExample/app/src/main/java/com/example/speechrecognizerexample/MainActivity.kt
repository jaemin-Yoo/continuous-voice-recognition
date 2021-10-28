package com.example.speechrecognizerexample

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    //Button
    var btn_start: Button? = null

    //TextView
    var tv_result: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 권한 요청

        // 권한 요청
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO
                ), 1
            )
        }

        // RecognizerIntent 생성

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        tv_result = findViewById(R.id.tv_result)

        btn_start = findViewById(R.id.btn_start)
        btn_start!!.setOnClickListener(View.OnClickListener {
            val mRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
            mRecognizer.setRecognitionListener(listener)
            mRecognizer.startListening(intent)
        })
    }

    private val listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(bundle: Bundle) {
            // 준비
            Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@MainActivity, "Error : $message", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) {
                tv_result!!.text = matches[i]
            }
            Toast.makeText(this@MainActivity, matches[0], Toast.LENGTH_SHORT).show()
        }

        override fun onPartialResults(bundle: Bundle) {
            // 부분 인식 결과
        }

        override fun onEvent(i: Int, bundle: Bundle) {
            // 향후 이벤트 추가 예약
        }
    }
}