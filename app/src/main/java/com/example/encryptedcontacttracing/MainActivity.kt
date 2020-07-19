package com.example.encryptedcontacttracing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.security.MessageDigest
import java.util.*
import kotlin.experimental.and


class RecordTime {
    var startTime = System.currentTimeMillis()
    var endTime = System.currentTimeMillis()
    var placeCode:String = ""

    fun getEncrypt(target:String) : String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(target.toByteArray())
        val sb = StringBuilder()
        for (i in digest.indices) {
            sb.append(((digest[i] and 0xff.toByte()) + 0x100).toString(16).substring(1))
        }
        return sb.toString()
    }
}

private val timer = RecordTime()

lateinit var notificationManager:NotificationManager
lateinit var codeQueueManager:MainActivity.CodeQueueManager
lateinit var purpleBox:View
lateinit var codebtnGetQR:Button
var btnGetQRIsPressed = false


class MainActivity : AppCompatActivity() {
    inner class CodeQueueManager {   // 14일치의 코드를 저장하는데 사용할 큐 + 파일
        private val filename = "data"
        var codeQueue:Queue<String> = LinkedList()

        fun addItems(itemList:MutableList<String>) {
            for (item in itemList) {
                codeQueue.offer(item)
                while (codeQueue.size > 4032) {
                    codeQueue.poll()
                } // 4032
                println(codeQueue.size)
            }
            codeQueueManager.saveCodestoFile()
        }
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
        private fun saveCodestoFile(){
            val copiedQueue = codeQueue.toMutableList()
            val myFile = File(filename)
            if (myFile.exists()) {
                myFile.delete()
            }
            try {
                val os = openFileOutput(filename, Context.MODE_PRIVATE)
                os.write(copiedQueue.joinToString("\n").toByteArray())
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        fun clearQueue() {
            while (codeQueue.size > 0) {
                codeQueue.remove()
            }
            saveCodestoFile()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        codebtnGetQR = findViewById(R.id.btnGetQR)
        btnGetQR
        val btnViewCodes = findViewById<Button>(R.id.btnViewCodes)
        val testremove = findViewById<Button>(R.id.testremove)
        val testupload = findViewById<Button>(R.id.dbupload)
        val testdownload = findViewById<Button>(R.id.dbdownload)
        purpleBox = findViewById<View>(R.id.box)

        val qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator.setOrientationLocked(false)

        codeQueueManager = CodeQueueManager()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alert = DatabaseManager(this)
        alert.loadFromDB()

        if (codeQueueManager.codeQueue.size == 0) {
            codeQueueManager.loadCodesfromFile()
        }

        codebtnGetQR.setOnClickListener {
            if (btnGetQRIsPressed) {
                val stopRecordingIntent = Intent(this, StopRecordingBroadcastReceiver::class.java)
                sendBroadcast(stopRecordingIntent)
            }
            else{
                qrScanIntegrator.initiateScan()
            }
        }
        btnViewCodes.setOnClickListener {
            val showCodesActivityIntent = Intent(this, ShowCodes::class.java)
                .putExtra("queue", codeQueueManager.codeQueue.joinToString("\n"))
            startActivity(showCodesActivityIntent)
        }

        testremove.setOnClickListener{
            codeQueueManager.clearQueue()
            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
        }
        testdownload.setOnClickListener{
            alert.update()
        }
        testupload.setOnClickListener{
            alert.sendToDB(codeQueueManager.codeQueue.toList())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result: IntentResult =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            try {
                timer.startTime = System.currentTimeMillis()
                timer.placeCode = timer.getEncrypt(result.contents)
                notifyRecording()
                box.setBackgroundColor(Color.rgb(114,137,218))

                btnGetQRIsPressed = true
                codebtnGetQR.text = "STOP"
            } catch (e: NumberFormatException) {
                val toastText = "올바르지 않은 QR코드입니다"
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun notifyRecording() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val notificationPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val stopRecordingIntent = Intent(this, StopRecordingBroadcastReceiver::class.java)
        val stopRecordingPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, stopRecordingIntent, 0)

        val notificationBuilder = NotificationCompat.Builder(this, "10001")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("다중이용시설에 있습니다")
            .setContentText("다중이용시설에 있습니다. 나가실 때 꺼주세요.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notificationPendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_background, "STOP", stopRecordingPendingIntent)

        val channel = NotificationChannel("10001", "CovidNotification", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(10001, notificationBuilder.build())

    }
}

val stop = StopRecording()

class StopRecordingBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        stop.stopRecording(notificationManager)
    }
}

class StopRecording{
    fun stopRecording(notificationManager:NotificationManager) {
        notificationManager.cancel(10001)
        timer.endTime = System.currentTimeMillis()
        val timeInterval = 1000 //300000

        val result = generateCodes(
            timer.startTime / timeInterval,
            timer.endTime / timeInterval,
            timer.placeCode
        )
        codeQueueManager.addItems(result)

        purpleBox.setBackgroundColor(Color.rgb(35,39,42))
        btnGetQRIsPressed = false
        codebtnGetQR.text = "QR코드 찍기"
    }

    private fun generateCodes(startTime:Long, endTime:Long, placeCode:String): MutableList<String> {
        val codelist = mutableListOf<String>()
        for (time in startTime..endTime) {
            val code = timer.getEncrypt(placeCode+time.toString())
            codelist.add(code)
        }
        return codelist
    }
}