package com.example.sttexample

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "STT_Log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        val start_stt = findViewById<Button>(R.id.start_STT)
        val stop_stt = findViewById<Button>(R.id.stop_STT)

        // STTButton 클릭시 startSTT() 함수 실행
        start_stt.setOnClickListener {
            Log.d(TAG, "음성인식 시작")
            Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Foreground::class.java)
            ContextCompat.startForegroundService(this, intent)
        }
        stop_stt.setOnClickListener {
            Log.d(TAG, "음성인식 종료")
            Toast.makeText(this@MainActivity, "음성인식 종료",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Foreground::class.java)
            stopService(intent)
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // 거부해도 계속 노출됨. ("다시 묻지 않음" 체크 시 노출 안됨.)
            // 허용은 한 번만 승인되면 그 다음부터 자동으로 허용됨.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.RECORD_AUDIO), 0)
        }
    }
}