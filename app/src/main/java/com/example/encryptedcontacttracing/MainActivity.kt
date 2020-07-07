package com.example.encryptedcontacttracing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.util.*




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetQR = findViewById<Button>(R.id.btnGetQR)
        val btnViewCodes = findViewById<Button>(R.id.btnViewCodes)

        val qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator.setOrientationLocked(false)

        btnGetQR.setOnClickListener {
//            val data = qrScanIntegrator.initiateScan()
            getEncryptCodes(1234)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        }
    }

    fun getEncryptCodes(code:Int) {
        val testLabel: TextView = findViewById(R.id.test)
        val time = System.currentTimeMillis()
        val seed = time * code
        val randomCode = Random(seed).nextLong()
        testLabel.text = randomCode.toString()
    }
}
