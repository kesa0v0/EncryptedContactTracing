package com.example.encryptedcontacttracing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ShowCodes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_codes)

        val codeview = findViewById<TextView>(R.id.codeView)

        codeview.text = intent.extras.getString("queue")
    }
}
