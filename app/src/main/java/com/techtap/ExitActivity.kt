package com.techtap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.techtap.base.BaseActivity

class ExitActivity : BaseActivity() {

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ExitActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.run {
                activity.startActivity(this)
                activity.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}