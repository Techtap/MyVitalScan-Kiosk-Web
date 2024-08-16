package com.techtap.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.techtap.SplashActivity

class BootReceiver : BroadcastReceiver() {

    val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: " + intent.action)
        Log.e("onReceive", "onReceive")
        Toast.makeText(context, "BootReceiver Called", Toast.LENGTH_SHORT).show()


        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val intent = Intent(context, SplashActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
            context.startActivity(intent)
        }

//        Intent(context, SplashActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        }.run {
//            context.startActivity(this)
//        }
    }

}