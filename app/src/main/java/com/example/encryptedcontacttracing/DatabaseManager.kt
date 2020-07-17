package com.example.encryptedcontacttracing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DatabaseManager(private val context: Context) {
    private lateinit var dataSnapshot:DataSnapshot
    private val database = Firebase.database.reference.child("Infected")
    private val codeListener = object : ValueEventListener {
        override fun onDataChange(Snapshot: DataSnapshot) {
            dataSnapshot = Snapshot
            checkInfect()
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Cannot Load DB", "loadPost:onCancelled", error.toException())
        }
    }
    fun checkInfect(){
        val db = dataSnapshot
        val dbCodes = mutableSetOf<String>()
        for (child in db.children) {
            if (child!=null && child.value!= null){
                dbCodes.addAll(child.value as Collection<String>)
            }
        }
        println(dbCodes)
        val dbCodesSet = dbCodes.toSet()
        val fileCodes = codeQueueManager.codeQueue.toSet()
        val intersection = fileCodes - (fileCodes - dbCodesSet)

        if (intersection.isNotEmpty()) {
            showNotify()
        } else {
            Toast.makeText(context, "없음", Toast.LENGTH_SHORT).show()
        }
    }
    fun loadFromDB() {
            database.addValueEventListener(codeListener)
        }
    fun sendToDB(codes:List<String>){
        database.push().setValue(codes)
        Toast.makeText(context, "Sent", Toast.LENGTH_SHORT).show()
    }
    fun update() {
        checkInfect()
    }
    private fun showNotify() {
        val quarantineBuilder = NotificationCompat.Builder(context, "10001")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("자가격리 대상자.")
            .setContentText("자가격리 대상자입니다. 보건소로 연락하세요")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setOngoing(true)

        val channel = NotificationChannel("10002", "Quarantine", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(10002, quarantineBuilder.build())
    }
}