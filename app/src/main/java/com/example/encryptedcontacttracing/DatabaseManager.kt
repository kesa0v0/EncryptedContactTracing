package com.example.encryptedcontacttracing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DatabaseManager(private val context: Context) {
    private lateinit var dataSnapshot:DataSnapshot
    private val database = Firebase.database.reference
    private val postListener = object : ValueEventListener {
        override fun onDataChange(Snapshot: DataSnapshot) {
            dataSnapshot = Snapshot
            checkInfect()
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Cannot Load DB", "loadPost:onCancelled", error.toException())
        }
    }
    fun checkInfect(){
//        val listIndicator = object: GenericTypeIndicator<List<String>>(){}
//        val dbCodes = dataSnapshot.child("Infected").getValue(listIndicator)
        val dbCodes = setOf<String>("2d71332250477ef676955ce6c057b384fdb5106957676c284f627c2f",
        "2d38631e46b172a416a794d20f2c3d4c16e53b413dca1")
        if (dbCodes != null){
            val dbCodesSet = dbCodes.toSet()
            val fileCodes = codeQueueManager.codeQueue.toSet()
            val intersection = fileCodes - (fileCodes - dbCodesSet)

            if (intersection.isNotEmpty()) {
                showNotify()
            }
        }
    }
    fun loadFromDB() {
        database.addValueEventListener(postListener)
    }
    fun sendToDB(codes:List<String>){
        database.child("Infected").setValue(codes)
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