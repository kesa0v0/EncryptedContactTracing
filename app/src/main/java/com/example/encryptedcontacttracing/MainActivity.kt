package com.example.encryptedcontacttracing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.NotificationCompat

import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and
import java.io.*



class RecordTime {
    var startTime = System.currentTimeMillis()
    var endTime = System.currentTimeMillis()
    var placeCode:Long = 0
}
private val timer = RecordTime()

private lateinit var notificationManager:NotificationManager

class MainActivity : AppCompatActivity() {
    val queue = CodeFile()

    inner class CodeFile {   // 14일치의 코드를 저장하는데 사용할 큐 + 파일
        private val filename = "data"
        var codeQueue:Queue<String> = LinkedList()

        fun loadCodesfromFile() {
            try {
                val buffer = BufferedReader(InputStreamReader(openFileInput(filename)))
                var str = buffer.readLine()
                while (str != null) {
                    codeQueue.offer(str)
                    str = buffer.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        fun saveCodestoFile(){
            try {
                val os = openFileOutput(filename, Context.MODE_PRIVATE)
                while (codeQueue.peek() != null) {
                    os.write(codeQueue.poll().toString().toByteArray())
                }
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetQR = findViewById<Button>(R.id.btnGetQR)
        val btnViewCodes = findViewById<Button>(R.id.btnViewCodes)

        val qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator.setOrientationLocked(false)

        btnGetQR.setOnClickListener {
            //            val data = qrScanIntegrator.initiateScan()
//            getEncryptCodes(1234)
            timer.startTime = System.currentTimeMillis()
            timer.placeCode = 1234
            notifyRecording()
        }
        btnViewCodes.setOnClickListener {
            val showCodesActivityIntent = Intent(this, ShowCodes::class.java)
            startActivity(showCodesActivityIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        }
    }

    private fun notifyRecording() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val stopRecordingIntent = Intent(this, StopRecordingBroadcastReceiver::class.java)
        val stopRecordingPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, stopRecordingIntent, 0)

        val builder = NotificationCompat.Builder(this, "10001")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("다중이용시설에 있습니다")
            .setContentText("다중이용시설에 있습니다. 나가실 때 꺼주세요.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_background, "STOP", stopRecordingPendingIntent)

        val channel = NotificationChannel("10001", "Test", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(1234, builder.build())

    }
}

val stop = StopRecording()

class StopRecordingBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("testing")
        stop.stopRecording(notificationManager)
    }
}

class StopRecording{
    fun stopRecording(notificationManager:NotificationManager) {
        notificationManager.cancel(1234)
        timer.endTime = System.currentTimeMillis()
        val timeInterval = 300000
        val result = generateCodes(
            timer.startTime % timeInterval,
            timer.endTime % timeInterval,
            timer.placeCode
        )
    }

    private fun generateCodes(startTime:Long, endTime:Long, placeCode:Long): MutableList<String> {
        val codelist = mutableListOf<String>()
        for (time in startTime..endTime) {
            codelist.add(getEncrypt((time*placeCode).toString()))
        }
        return codelist
    }

    private fun getEncrypt(target:String) : String{
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(target.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(((digest[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }
}