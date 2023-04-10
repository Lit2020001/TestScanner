package com.team.testscanner.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.team.testscanner.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.text_view)
        val intent = Intent(this, question::class.java)
        startActivity(intent)
        tv.setOnClickListener {
            val intent = Intent(this, question::class.java)
            startActivity(intent)
        }
    }

}