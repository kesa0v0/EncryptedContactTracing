package com.example.encryptedcontacttracing

import android.app.NotificationManager


class Alert(codeQueueManager: MainActivity.CodeQueueManager, notificationManager: NotificationManager) {
    fun loadFromDB():Set<String> {
        return
    }
    fun showNotify() {

    }
    fun update() {
        val fileCodes = codeQueueManager.codeQueue.toSet()
        val DBCodes = loadFromDB()

        if ((fileCodes - (fileCodes + DBCodes)).isNotEmpty()) {
            showNotify()
        }
    }
}