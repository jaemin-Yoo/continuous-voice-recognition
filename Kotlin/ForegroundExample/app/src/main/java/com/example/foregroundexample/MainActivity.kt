package com.example.foregroundexample

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    //Button
    var btn_start: Button? = null
    var btn_stop: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_start = findViewById(R.id.btn_start)
        btn_stop = findViewById(R.id.btn_stop)

        btn_start!!.setOnClickListener(View.OnClickListener {
            val serviceIntent = Intent(this@MainActivity, MyService::class.java)
            startService(serviceIntent)
            Toast.makeText(this@MainActivity, "Service start", Toast.LENGTH_SHORT).show()
        })

        btn_stop!!.setOnClickListener(View.OnClickListener {
            val serviceIntent = Intent(this@MainActivity, MyService::class.java)
            stopService(serviceIntent)
            Toast.makeText(this@MainActivity, "Service stop", Toast.LENGTH_SHORT).show()
        })
    }
}