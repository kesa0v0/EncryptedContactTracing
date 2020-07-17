package com.example.encryptedcontacttracing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ShowCodes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_codes)

        val codeview = findViewById<TextView>(R.id.codeView)

        val codes = intent.extras.getString("queue").split("\n")
        var text = ""
        for (index in 0 until codes.size) {
            text += "${index.toString()}: ${codes[index]}\n"
        }
        codeview.text = text
    }
}
