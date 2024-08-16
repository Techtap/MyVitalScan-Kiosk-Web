package com.techtap

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityScanEndingBinding
import com.techtap.databinding.ActivityScanEndingRBinding
import com.techtap.databinding.ActivityScreenSaverBinding
import com.techtap.databinding.ActivityScreenSaverRBinding
import com.techtap.network.ResponseStatus
import com.techtap.network.Status
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScreenSaverActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ScreenSaverActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    private lateinit var binding: ViewDataBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var measurementDetail: MeasurementResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_screen_saver)
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
//        initKeyBoard()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_screen_saver_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_screen_saver)
        }
        initClick()
    }

    private fun initKeyBoard() {
        if (binding is ActivityScreenSaverBinding) {
            (binding as ActivityScreenSaverBinding).keyboardView.visibility = View.VISIBLE
        } else if (binding is ActivityScreenSaverRBinding) {
            (binding as ActivityScreenSaverRBinding).keyboardView.visibility = View.VISIBLE
        }
        val keyboard = CustomKeyboard(this, R.xml.keyboard)
        keyboard.registerKeyboardView(findViewById(R.id.keyboardView))
        keyboard.registerEditText(findViewById(R.id.et_scan_code), true)

    }

    private fun initClick() {
        if (binding is ActivityScreenSaverBinding) {
            (binding as ActivityScreenSaverBinding).onClickListener = this
        } else if (binding is ActivityScreenSaverRBinding) {
            (binding as ActivityScreenSaverRBinding).onClickListener = this
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.et_scan_code -> {
                initKeyBoard()
            }

            R.id.cl_screen_saver -> {
                PrivacyPolicyTermsConditionActivity.startActivity(this)
            }

            R.id.btn_see_results -> {
//                if (binding is ActivityScreenSaverBinding) {
//                    (binding as ActivityScreenSaverBinding).alertInvalidId.visibility = View.VISIBLE
//                    (binding as ActivityScreenSaverBinding).etScanCode.isEnabled = false
//                    (binding as ActivityScreenSaverBinding).btnSeeResults.isEnabled = false
//                } else if (binding is ActivityScreenSaverRBinding) {
//                    (binding as ActivityScreenSaverRBinding).alertInvalidId.visibility = View.VISIBLE
//                    (binding as ActivityScreenSaverRBinding).etScanCode.isEnabled = false
//                    (binding as ActivityScreenSaverRBinding).btnSeeResults.isEnabled = false
//                }
//                PrivacyPolicyTermsConditionActivity.startActivity(this)

                var scanId = ""
                if (binding is ActivityScreenSaverBinding) {
                    scanId = (binding as ActivityScreenSaverBinding).etScanCode.text.toString()
                } else if (binding is ActivityScreenSaverRBinding) {
                    scanId = (binding as ActivityScreenSaverRBinding).etScanCode.text.toString()
                }

                if (scanId.isEmpty()) {
                    Utils.showErrorMessage(this, getString(R.string.please_enter_scan_id))
                } else {
                    getScanInfo(scanId)
                }
            }

            R.id.btn_close -> {
                if (binding is ActivityScreenSaverBinding) {
                    (binding as ActivityScreenSaverBinding).alertInvalidId.visibility = View.GONE
                    (binding as ActivityScreenSaverBinding).etScanCode.isEnabled = true
                    (binding as ActivityScreenSaverBinding).btnSeeResults.isEnabled = true
                    (binding as ActivityScreenSaverBinding).etScanCode.isFocused
                    (binding as ActivityScreenSaverBinding).keyboardView.visibility = View.VISIBLE
                } else if (binding is ActivityScreenSaverRBinding) {
                    (binding as ActivityScreenSaverRBinding).alertInvalidId.visibility = View.GONE
                    (binding as ActivityScreenSaverRBinding).etScanCode.isEnabled = true
                    (binding as ActivityScreenSaverRBinding).btnSeeResults.isEnabled = true
                    (binding as ActivityScreenSaverRBinding).etScanCode.isFocused
                    (binding as ActivityScreenSaverRBinding).keyboardView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getScanInfo(scanId: String) {
        if (Utils.isNetworkAvailable(this)) {
//            if (binding is ActivityScanEndingBinding) {
//                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 0
//            } else if (binding is ActivityScanEndingRBinding) {
//                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 0
//            }
            onBoardingViewModel.getScanInfo(scanId)
        } else {
//            if (binding is ActivityScanEndingBinding) {
//                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 3
//            } else if (binding is ActivityScanEndingRBinding) {
//                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 3
//            }
        }
    }


    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.getScanInfoState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null && it.data.basicInfo != null) {
                            measurementDetail = it.data
                            FullReportActivity.startActivity(this@ScreenSaverActivity, measurementDetail)
                        } else {
//                            Utils.showErrorMessage(this@ScreenSaverActivity, it.data?.message)
                            showScanIdInvalid()
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        showScanIdInvalid()
//                        Utils.showErrorMessage(this@ScreenSaverActivity, it.message)
                    }
                }
            }
        }
    }

    private fun showScanIdInvalid() {
        if (binding is ActivityScreenSaverBinding) {
            (binding as ActivityScreenSaverBinding).alertInvalidId.visibility = View.VISIBLE
            (binding as ActivityScreenSaverBinding).etScanCode.isEnabled = false
            (binding as ActivityScreenSaverBinding).btnSeeResults.isEnabled = false
        } else if (binding is ActivityScreenSaverRBinding) {
            (binding as ActivityScreenSaverRBinding).alertInvalidId.visibility = View.VISIBLE
            (binding as ActivityScreenSaverRBinding).etScanCode.isEnabled = false
            (binding as ActivityScreenSaverRBinding).btnSeeResults.isEnabled = false
        }
    }

}