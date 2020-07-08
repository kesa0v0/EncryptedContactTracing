package com.example.encryptedcontacttracing

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import jdk.nashorn.internal.objects.ArrayBufferView.buffer
import jdk.nashorn.internal.runtime.ScriptingFunctions.readLine
import android.R.attr.data
import sun.text.normalizer.UTF16.append
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




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
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        }
    }

    fun getEncryptCodes(code: Int) {
        val testLabel: TextView = findViewById(R.id.test)
        val time = System.currentTimeMillis()
        val seed = time * code
        val randomCode = Random(seed).nextLong()


    }

    fun readFile(filename: String):String {
        try {
            val buffer = BufferedReader(InputStreamReader(openFileInput(filename)))
            var data = ""
            var str = buffer.readLine()
            while (str != null) {
                data = data + str + "\n"
                str = buffer.readLine()
            }
            return data
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun writeFile(filename:String, content:ByteArray) {
        try {
            val os = openFileOutput(filename, Context.MODE_PRIVATE)
            os.write(content)
            os.close()
            } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
