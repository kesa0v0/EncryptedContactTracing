package com.example.encryptedcontacttracing

import android.app.NotificationManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase



class DatabaseManager(codeQueueManager: MainActivity.CodeQueueManager, notificationManager: NotificationManager) {
    val database = Firebase.database.reference
    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val dbCodes = dataSnapshot.getValue<List<String>>()
            if (dbCodes != null){
                val dbCodesSet = dbCodes.toSet()
                val fileCodes = codeQueueManager.codeQueue.toSet()

                if ((fileCodes - (fileCodes + dbCodesSet)).isNotEmpty()) {
                    showNotify()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Cannot Load DB", "loadPost:onCancelled", error.toException())
        }
    }

    fun loadFromDB():Set<String> {
        // TODO : Bring infected Code from Firebase
        return setOf()
    }
    fun sendtoDB(codes:List<String>){
        database.child("Infected").setValue(codes)
    }
    fun showNotify() {
        // TODO : Show Notification
        println("Notify")
    }
    fun update() {
        val DBCodes = loadFromDB()
        database.addValueEventListener(postListener)

    }
}