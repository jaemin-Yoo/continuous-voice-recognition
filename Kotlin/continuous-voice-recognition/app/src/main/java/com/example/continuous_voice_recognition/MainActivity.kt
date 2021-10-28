package com.example.continuous_voice_recognition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val TAG = "MainTag"

    //Button
    var btn_start: Button? = null
    var btn_stop: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        btn_start = findViewById(R.id.btn_start)
        btn_start!!.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@MainActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, Foreground::class.java)
            startService(intent)
        })

        btn_stop = findViewById(R.id.btn_stop)
        btn_stop!!.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@MainActivity, "음성인식 종료", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity, Foreground::class.java)
            stopService(intent)
        })
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }
}