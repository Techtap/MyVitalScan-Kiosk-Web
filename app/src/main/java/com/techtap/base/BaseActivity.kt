package com.techtap.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.techtap.ScanActivity
import com.techtap.SplashActivity
import com.techtap.databinding.LayoutNoInternetBinding
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.CustomProgressDialog
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {

    private lateinit var tvToolbarTitle: AppCompatTextView
    lateinit var mActivity: Activity
    private var timeOutHandler: Handler? = null

    private var timeOutValue: Long = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
//        if (this is SplashActivity || this is ConnectToWalletActivity) {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//        }

        if (this !is SplashActivity) {
            if (this is ScanActivity) {
                timeOutValue = 90000
            } else {
                timeOutValue = 60000
            }
            startTimeOut()
        }
    }


    override fun onUserInteraction() {
        super.onUserInteraction()
        if (this !is SplashActivity) {
            startTimeOut()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this !is SplashActivity) {
            stopTimeOut()
        }
    }

    protected fun initNoInternet(noInternetLayout: LayoutNoInternetBinding, onClickListener: View.OnClickListener) {
        noInternetLayout.onClickListener = onClickListener
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
//                if (this is AttachedPhotosActivity) {
//                    (this as AttachedPhotosActivity).toolbarBackPressed()
//                } else if (this is DeliveryActionsListActivity) {
//                    (this as DeliveryActionsListActivity).toolbarBackPressed()
//                } else {
//                    finish()
//                }
                finish()
                true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
//        AnimationsHandler.playBaseActivityAnimation(this)
    }

    protected fun hideKeyboard(v: EditText) {
        val imm: InputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun showHideProgress(show: Boolean) {
        if (show) {
            CustomProgressDialog.showProgressDialog(this)
        } else {
            CustomProgressDialog.dismissProgressDialog()
        }
    }

    private fun startTimeOut() {
        stopTimeOut()
        timeOutHandler = Handler(Looper.getMainLooper())
        timeOutHandler?.postDelayed(object : Runnable {
            override fun run() {
//                Utils.showToast(this@PrivacyPolicyTermsConditionActivity, "TIMER CALLED")
                SplashActivity.startActivityClearTop(this@BaseActivity)
            }
        }, timeOutValue)
    }

    private fun stopTimeOut() {
        try {
            if (timeOutHandler != null) {
                timeOutHandler?.removeCallbacksAndMessages(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}