package com.techtap.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.techtap.R
import com.techtap.databinding.ActivityScanBinding


object CustomProgressDialog {
    private var progressDialog: Dialog? = null

    fun showProgressDialog(mContext: Context?) {
        dismissProgressDialog()
        progressDialog = null
        System.gc()
        if (mContext != null) {
            progressDialog = Dialog(mContext)
            progressDialog!!.show()
            progressDialog!!.setContentView(R.layout.custom_progressdialog)

            val ivLoading = progressDialog!!.findViewById<ImageView>(R.id.iv_loading)
            Glide.with(mContext).load(R.raw.ic_loading1).into(ivLoading)

            if (progressDialog!!.window != null) {
                progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            }
            progressDialog!!.setCancelable(false)
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

//    fun setProgressDialogText(text: String) {
//        val textView = progressDialog!!.findViewById<TextView>(R.id.textView1)
//        textView.text = text
//    }

}
