package com.example.encryptedcontacttracing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.google.zxing.integration.android.IntentIntegrator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO:Get Camera Access

        val btnGetQR = findViewById<Button>(R.id.btnGetQR)
        val btnViewCodes = findViewById<Button>(R.id.btnViewCodes)

        val qrScanIntegrator = IntentIntegrator(this)

        btnGetQR.setOnClickListener {
            val data = qrScanIntegrator.initiateScan()
        }
    }
}
