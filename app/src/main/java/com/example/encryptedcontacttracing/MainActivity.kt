package com.example.encryptedcontacttracing

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.core.os.postDelayed

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetQR = findViewById<Button>(R.id.btnGetQR)
        val btnViewCodes = findViewById<Button>(R.id.btnViewCodes)

        val qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator.setOrientationLocked(false)

        val handler =  Handler()
        val delayMillis:Long = 300000
// Define the code block to be executed
        val runnableCode = Runnable {
            @Override
            fun run() {
                // Do something here on the main thread
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, delayMillis)
            }
        }

        btnGetQR.setOnClickListener {
            //            val data = qrScanIntegrator.initiateScan()
            getEncryptCodes(1234)
            handler.post(runnableCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getEncryptCodes(code: Int) {
        val testLabel: TextView = findViewById(R.id.test)
        val time = System.currentTimeMillis() / 300000
        val seed = time * code
        val randomCode = Random(seed).nextLong()

        val test = """
            1, 2
            3, 4
            5, 6
            """.trimIndent()
        val result = turnFileToMap(test)
        testLabel.text = makeFileContent(result)
    }

    private fun makeFileContent(map: MutableMap<Long, Long>): String {
        var content:String = ""
        for ((key, value) in map) {
            content = "$content$key, $value\n"
        }
        return content
    }

    private fun turnFileToMap(file:String): MutableMap<Long, Long> {
        val kvlist = file.split('\n')
        var codeMap = mutableMapOf<Long, Long>()
        for (kv in kvlist) {
            println(kvlist)
            val key = kv.split(',')[0].trim().toLong()
            val value = kv.split(',')[1].trim().toLong()
            codeMap[key] = value
        }
        return codeMap
    }

    fun readFile(filename: String): String? {
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
        return null
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
