package com.techtap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.pouyaheydari.appupdater.AppUpdaterDialog
import com.pouyaheydari.appupdater.core.pojo.Store
import com.pouyaheydari.appupdater.core.pojo.StoreListItem
import com.pouyaheydari.appupdater.core.pojo.Theme
import com.pouyaheydari.appupdater.pojo.UpdaterDialogData
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityScreenSaverBinding
import com.techtap.databinding.ActivityScreenSaverRBinding
import com.techtap.databinding.ActivitySplashBinding
import com.techtap.network.Status
import com.techtap.utils.Constant
import com.techtap.utils.Logger
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySplashBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var measurementDetail: MeasurementResponse? = null

    companion object {

        fun startActivityClearTop(activity: Activity) {
            Intent(activity, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            }.run {
                activity.startActivity(this)
                activity.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        initClick()
        initObserver()

        binding.tvAppSdkVersion.text =
            "${getString(R.string.app_version)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}) | ${
                getString(R.string.sdk_version)
            } ${ai.binah.sdk.BuildConfig.VERSION_NAME} (${ai.binah.sdk.BuildConfig.VERSION_CODE})"


//        val request = VersionCheckRequest(
//            BuildConfig.APPLICATION_ID, BuildConfig.VERSION_NAME
//        )
//        onBoardingViewModel.sendVersionCheckRequest(request)
    }


    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.sendVersionCheckState.collect {
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
//                        showHideProgress(false)
                        if (it.data?.data?.hasUpdateAvailable == true) {
//                            Utils.showToast(this@SplashActivity, "Update Available ${it.data.data?.version + " > " + Constant.SAMPLE_VERSION_CODE}")
                            checkUpdate(it.data?.data)
                        }
                    }

                    Status.ERROR -> {
//                        showHideProgress(false)
//                        Utils.showErrorMessage(this@SplashActivity, it.message)
                    }
                }
            }
        }

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
                            FullReportActivity.startActivity(this@SplashActivity, measurementDetail)
                        } else {
                            showScanIdInvalid(false)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        showScanIdInvalid(false)
                    }
                }
            }
        }
    }

    private fun showScanIdInvalid(isEmptyCode: Boolean) {
        if (Pref.isLowView) {
            binding.alertInvalidIdR.visibility = View.VISIBLE
            binding.etScanCodeR.isEnabled = false
            binding.btnSeeResultsR.isEnabled = false

            if (isEmptyCode) {
                binding.tvInvalidCodeMessageR.text = getString(R.string.invalid_code_message)
            } else {
                binding.tvInvalidCodeMessageR.text = getString(R.string.provided_scan)
            }

        } else {
            binding.alertInvalidId.visibility = View.VISIBLE
            binding.etScanCode.isEnabled = false
            binding.btnSeeResults.isEnabled = false

            if (isEmptyCode) {
                binding.tvInvalidCodeMessage.text = getString(R.string.invalid_code_message)
            } else {
                binding.tvInvalidCodeMessage.text = getString(R.string.provided_scan)
            }
        }
        /*if (binding is ActivityScreenSaverBinding) {
            (binding as ActivityScreenSaverBinding).alertInvalidId.visibility = View.VISIBLE
            (binding as ActivityScreenSaverBinding).etScanCode.isEnabled = false
            (binding as ActivityScreenSaverBinding).btnSeeResults.isEnabled = false
        } else if (binding is ActivityScreenSaverRBinding) {
            (binding as ActivityScreenSaverRBinding).alertInvalidId.visibility = View.VISIBLE
            (binding as ActivityScreenSaverRBinding).etScanCode.isEnabled = false
            (binding as ActivityScreenSaverRBinding).btnSeeResults.isEnabled = false
        }*/
    }


    private fun checkUpdate(versionCheck: VersionCheck?) {
        //   make a list of stores

        if (!versionCheck?.apkUrl.isNullOrEmpty() && !versionCheck?.packageName.isNullOrEmpty()) {
            val list = arrayListOf<StoreListItem>()
            list.add(
                StoreListItem(
                    store = Store.DIRECT_URL,
                    title = getString(R.string.download),
                    icon = R.mipmap.ic_launcher,
                    url = versionCheck?.apkUrl!!,
                    packageName = versionCheck.packageName!!
                )
            )

            val TAG = "showUpdateDialogTag"
            // creating update dialog
            AppUpdaterDialog.getInstance(
                UpdaterDialogData(
                    title = getString(R.string.app_name),
                    description = getString(R.string.new_version_is_available),
                    storeList = list,
                    isForceUpdate = false,
                    typeface = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf"),
                    theme = Theme.LIGHT,
                ),
            ).show(supportFragmentManager, TAG)
        }

    }

    private fun initClick() {
        binding.onClickListener = this
    }

    private fun playVideo() {
        if (Pref.isLowView) {
            binding.videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.screensaver_lv))
        } else {
            binding.videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.screensaver_sv1))
        }

        val mediaController = MediaController(this)
        mediaController.visibility = View.GONE
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
//        binding.videoView.requestFocus()
        binding.videoView.start()
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.video_view, R.id.rl_video_view -> {
                //               PrivacyPolicyTermsConditionActivity.startActivity(this)
//                ScanEndingActivity.startActivity(this, 0)
//                FullReportActivity.startActivity(this)
//                LearnMoreActivity.startActivity(this)

//                ScreenSaverActivity.startActivity(this)

//                if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
//                    ScreenSaverActivity.startActivity(this)
//                } else {
//                    PrivacyPolicyTermsConditionActivity.startActivity(this)
//                }

                if (BuildConfig.FLAVOR.equals(Constant.EXTERNAL, true)) {
//                    PrivacyPolicyTermsConditionActivity.startActivity(this)
                    TransactionActivity.startActivity(this)
                }
            }

            R.id.et_scan_code, R.id.et_scan_code_r -> {
                initKeyBoard()
            }

            R.id.cl_internal, R.id.cl_internal_r -> {
                PrivacyPolicyTermsConditionActivity.startActivity(this)
//                ScanActivity.startActivity(this)
//                ScanEndingActivity.startActivity(this@SplashActivity, 0)
            }

            R.id.btn_see_results, R.id.btn_see_results_r -> {
                var scanId = ""
                scanId = if (Pref.isLowView) {
                    binding.etScanCodeR.text.toString()
                } else {
                    binding.etScanCode.text.toString()
                }

                if (scanId.isEmpty()) {
//                    Utils.showErrorMessage(this, getString(R.string.please_enter_scan_id))
                    showScanIdInvalid(true)
                } else {
                    getScanInfo(scanId)
                }
            }

            R.id.btn_close, R.id.btn_close_r -> {
                if (Pref.isLowView) {
                    binding.alertInvalidIdR.visibility = View.GONE
                    binding.etScanCodeR.isEnabled = true
                    binding.btnSeeResultsR.isEnabled = true
                    binding.etScanCodeR.isFocused
                    binding.keyboardViewR.visibility = View.VISIBLE
                } else {
                    binding.alertInvalidId.visibility = View.GONE
                    binding.etScanCode.isEnabled = true
                    binding.btnSeeResults.isEnabled = true
                    binding.etScanCode.isFocused
                    binding.keyboardView.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun getScanInfo(scanId: String) {
        if (Utils.isNetworkAvailable(this)) {
            onBoardingViewModel.getScanInfo(scanId)
        } else {
            Utils.showErrorMessage(this, getString(R.string.no_internet_connection))
        }
    }


    private fun initKeyBoard() {
//        if (binding is ActivityScreenSaverBinding) {
//            (binding as ActivityScreenSaverBinding).keyboardView.visibility = View.VISIBLE
//        } else if (binding is ActivityScreenSaverRBinding) {
//            (binding as ActivityScreenSaverRBinding).keyboardView.visibility = View.VISIBLE
//        }
        if (Pref.isLowView) {
            binding.keyboardViewR.visibility = View.VISIBLE
            val keyboard = CustomKeyboard(this, R.xml.keyboard)
            keyboard.registerKeyboardView(findViewById(R.id.keyboardView_r))
            keyboard.registerEditText(findViewById(R.id.et_scan_code_r), true)
        } else {
            binding.keyboardView.visibility = View.VISIBLE
            val keyboard = CustomKeyboard(this, R.xml.keyboard)
            keyboard.registerKeyboardView(findViewById(R.id.keyboardView))
            keyboard.registerEditText(findViewById(R.id.et_scan_code), true)
        }


    }


    override fun onResume() {
        super.onResume()

//        Handler(Looper.getMainLooper()).postDelayed({
//            playVideo()
//            binding.videoView.setOnCompletionListener {
//                Logger.errorLog("setOnCompletionListener")
//                playVideo()
//            }
//        }, 1000)

        playVideo()
        binding.videoView.setOnCompletionListener {
            Logger.errorLog("setOnCompletionListener")
            playVideo()
        }

        if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
            binding.viewFlipper.visibility = View.VISIBLE

            if (Pref.isLowView) {
                binding.viewFlipper.displayedChild = 1
            } else {
                binding.viewFlipper.displayedChild = 0
            }
        } else {
            binding.viewFlipper.visibility = View.GONE
        }


    }

    override fun onDestroy() {
//        if (mediaPlayer != null) {
//            mediaPlayer?.stop()
//            mediaPlayer?.release()
//            mediaPlayer = null
//        }
        super.onDestroy()
    }

}