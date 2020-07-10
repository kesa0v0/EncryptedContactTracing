package com.example.encryptedcontacttracing

import android.os.Handler
import android.os.Looper

val handler =  Handler()
// Define the code block to be executed
private val runnableCode = Runnable {
    @Override
    fun run() {
        // Do something here on the main thread
        // Repeat this the same runnable code block again another 2 seconds
        // 'this' is referencing the Runnable object
        handler.postDelayed(this, 2000);
    }
};
// Start the initial runnable task by posting through the handler
handler.post(runnableCode)