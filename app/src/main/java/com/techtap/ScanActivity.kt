package com.techtap

import ai.binah.sdk.api.HealthMonitorException
import ai.binah.sdk.api.SessionEnabledVitalSigns
import ai.binah.sdk.api.alerts.AlertCodes
import ai.binah.sdk.api.alerts.ErrorData
import ai.binah.sdk.api.alerts.WarningData
import ai.binah.sdk.api.images.ImageData
import ai.binah.sdk.api.images.ImageListener
import ai.binah.sdk.api.images.ImageValidity
import ai.binah.sdk.api.license.LicenseDetails
import ai.binah.sdk.api.license.LicenseInfo
import ai.binah.sdk.api.session.Session
import ai.binah.sdk.api.session.SessionInfoListener
import ai.binah.sdk.api.session.SessionState
import ai.binah.sdk.api.vital_signs.VitalSign
import ai.binah.sdk.api.vital_signs.VitalSignTypes
import ai.binah.sdk.api.vital_signs.VitalSignsListener
import ai.binah.sdk.api.vital_signs.VitalSignsResults
import ai.binah.sdk.api.vital_signs.vitals.VitalSignBloodPressure
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPRQ
import ai.binah.sdk.api.vital_signs.vitals.VitalSignPulseRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignRespirationRate
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSDNN
import ai.binah.sdk.api.vital_signs.vitals.VitalSignSNSZone
import ai.binah.sdk.api.vital_signs.vitals.VitalSignStressLevel
import ai.binah.sdk.session.FaceSessionBuilder
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityScanBinding
import com.techtap.databinding.ActivityScanRBinding
import com.techtap.network.Status
import com.techtap.utils.BinahErrorMessage
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Enums
import com.techtap.utils.Logger
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanActivity : BaseActivity(), View.OnClickListener, ImageListener, VitalSignsListener, SessionInfoListener {

    private var mediaPlayer: MediaPlayer? = null
    private var timeOutHandler: Handler? = null
    private var isErrorOrWarning: Boolean = false

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, ScanActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    private val TAG = ScanActivity.javaClass::class.simpleName
    private val permissionsRequestCode = 12345

    private lateinit var binding: ViewDataBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()

    private var handlerPublishReport: Handler? = null
    private var isResultPublishedTimePassed = false
    private var faceResultID: Long = 0

    private var mTime = 0
    private var mTimeCountHandler: Handler? = null

    //    private var mWarningDialogTimeoutHandler: Handler? = null
    private var progressPercent: Double = 0.0

    private var session: Session? = null
    private var isErrorDialogShowing = false

    private val faceDetection: Bitmap? by lazy {
//        ContextCompat.getDrawable(this, R.drawable.face)?.toBitmap()
        ContextCompat.getDrawable(this, R.drawable.ic_scanning_bg)?.toBitmap()
    }


    private var enabledVitalSigns: SessionEnabledVitalSigns? = null
    private var isFromStop = false

    private var countDownTimer: CountDownTimer? = null
    val endTime = 10
    var currentProgress = 0
    var isStartAlignment = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        initObserver()
        playPrompt()

    }

    private fun initClick() {
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).onClickListener = this
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).onClickListener = this
        }
    }


    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val myVitalSignBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        val myVitalSignSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.my_vital_scan_is_designed))
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan1, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan2, 77, 89, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan3: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan3, 92, 95, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (binding is ActivityScanBinding) {

            (binding as ActivityScanBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityScanBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()

        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityScanRBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
        }


//        if (binding is ActivityPersonaSelectionBinding) {
//            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.text = myVitalSignSpan
//            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
//        } else if (binding is ActivityPersonaSelectionRBinding) {
//            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.text = myVitalSignSpan
//            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
//        }
    }

    private fun setValues() {
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).tvScanId.text = String.format(getString(R.string.scan_id_x), Pref.generateCodeObject?.generateCode)
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).tvScanId.text = String.format(getString(R.string.scan_id_x), Pref.generateCodeObject?.generateCode)
        }
    }


    private fun initObserver() {

//        lifecycleScope.launch {
//            onBoardingViewModel.beforeScanState.collect {
//                when (it.status) {
//                    Status.LOADING -> {
////                        showHideProgress(false)
//                    }
//
//                    Status.SUCCESS -> {
//                        if (it.code == 200 && it.data != null) {
//                            scanID = it.data.id
//                            startMeasuring()
//                        } else {
////                            showHideProgress(false)
//                            Utils.showErrorMessage(this@ScanActivity, it.message)
//                        }
//                    }
//
//                    Status.ERROR -> {
////                        showHideProgress(false)
//                        Utils.showErrorMessage(this@ScanActivity, it.message)
//                    }
//                }
//            }
//        }


        lifecycleScope.launch {
            onBoardingViewModel.scanStatusState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == 200 && it.data != null) {
                            if (isFromStop) {
                                isFromStop = false
                                stopTimeCount()
                                stopMeasuring()
                                closeSession()
                                finish()
                            }

                        } else {
                            Utils.showErrorMessage(this@ScanActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        Utils.showErrorMessage(this@ScanActivity, it.message)
                    }
                }
            }
        }


        lifecycleScope.launch {
            onBoardingViewModel.sendScanResultState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.data != null && it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS) {
                            faceResultID = it.data.id
                            ScanEndingActivity.startActivity(this@ScanActivity, 0)
                            finish()
//                            AnimationsHandler.playActivityAnimation(
//                                this@ScanActivity, AnimationsHandler.Animations.FadeEnter
//                            )
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        Utils.showErrorMessage(this@ScanActivity, it.message)
                    }
                }
            }
        }
    }


    private fun startTimeCount() {
        Log.e(TAG, "startTimeCount called")

        if (binding is ActivityScanBinding) {

//            (binding as ActivityScanBinding).clReadyToBegin.visibility = View.GONE
//            (binding as ActivityScanBinding).clScanningInProgress.visibility = View.VISIBLE
            (binding as ActivityScanBinding).clScanInfo.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).clScanProgress.visibility = View.VISIBLE
            (binding as ActivityScanBinding).lblScanStarted.visibility = View.VISIBLE

            (binding as ActivityScanBinding).clButtons.visibility = View.INVISIBLE
            if (isStartAlignment) {
                (binding as ActivityScanBinding).btnScan.visibility = View.INVISIBLE
//                (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
            } else {
                (binding as ActivityScanBinding).btnScan.visibility = View.VISIBLE
//                (binding as ActivityScanBinding).llScanReading.visibility = View.VISIBLE
            }


            (binding as ActivityScanBinding).tvScanningError.visibility = View.GONE
            (binding as ActivityScanBinding).tvVitalSignScanMessage.visibility = View.VISIBLE
            (binding as ActivityScanBinding).ivVitalSignScan.visibility = View.VISIBLE
            (binding as ActivityScanBinding).readingProgressBar.visibility = View.VISIBLE


//            (binding as ActivityScanBinding).tvScanMessage.visibility = View.VISIBLE
            (binding as ActivityScanBinding).tvScanReading.visibility = View.VISIBLE
            (binding as ActivityScanBinding).tvScanReadingUnit.visibility = View.VISIBLE

//            (binding as ActivityScanBinding).tvScanMessage.text = getString(R.string.scanning_in_progress_stay_still)

            (binding as ActivityScanBinding).ivCameraView.visibility = View.GONE
//            Glide.with(this).load(R.drawable.ic_scanning_bg).into((binding as ActivityScanBinding).ivCameraView)
            Glide.with(this).load(R.raw.ic_vital_sign_all).into((binding as ActivityScanBinding).ivVitalSignScan)


            if (mTimeCountHandler != null) {
                mTimeCountHandler?.removeCallbacksAndMessages(null)
            }
            mTime = 0
            progressPercent = 0.0
            mTimeCountHandler = Handler(Looper.getMainLooper())
            mTimeCountHandler?.post(object : Runnable {
                override fun run() {
//                    Logger.e("IN startTimeCount", "" + isStartAlignment)
                    if (!isStartAlignment) {
                        mTime++
                        updateReadingProgressMsg()
                        try {
//                        progressPercent = ((mTime.toDouble() / Constant.BINAH_AI_SCANNING_TIME_SECONDS) * 100)
                            progressPercent = ((mTime.toDouble() / BuildConfig.SCANNING_TIME) * 100)
                            (binding as ActivityScanBinding).readingProgressBar.setProgressPercentage(progressPercent, true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mTimeCountHandler?.postDelayed(this, 1000)
                }
            })
        } else if (binding is ActivityScanRBinding) {

//            (binding as ActivityScanRBinding).clReadyToBegin.visibility = View.GONE
//            (binding as ActivityScanRBinding).clScanningInProgress.visibility = View.VISIBLE

            (binding as ActivityScanRBinding).clScanInfo.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).clStartIn.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).clScanProgress.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).lblScanStarted.visibility = View.VISIBLE

            (binding as ActivityScanRBinding).clButtons.visibility = View.INVISIBLE
            if (isStartAlignment) {
                (binding as ActivityScanRBinding).btnScan.visibility = View.INVISIBLE
//                (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
            } else {
                (binding as ActivityScanRBinding).btnScan.visibility = View.VISIBLE
//                (binding as ActivityScanBinding).llScanReading.visibility = View.VISIBLE
            }

            (binding as ActivityScanRBinding).tvScanningError.visibility = View.GONE
            (binding as ActivityScanRBinding).tvVitalSignScanMessage.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).ivVitalSignScan.visibility = View.VISIBLE

//            (binding as ActivityScanRBinding).llScanReading.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).readingProgressBar.visibility = View.VISIBLE
//            (binding as ActivityScanRBinding).tvScanMessage.visibility = View.VISIBLE

            (binding as ActivityScanRBinding).tvScanReading.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).tvScanReadingUnit.visibility = View.VISIBLE

//            (binding as ActivityScanRBinding).tvScanMessage.text = getString(R.string.scanning_in_progress_stay_still)

            (binding as ActivityScanRBinding).ivCameraView.visibility = View.GONE
//            Glide.with(this).load(R.raw.ic_scanning_bg).into((binding as ActivityScanRBinding).ivCameraView)
            Glide.with(this).load(R.raw.ic_vital_sign_all).into((binding as ActivityScanRBinding).ivVitalSignScan)


            if (mTimeCountHandler != null) {
                mTimeCountHandler?.removeCallbacksAndMessages(null)
            }
            mTime = 0
            progressPercent = 0.0
            mTimeCountHandler = Handler(Looper.getMainLooper())
            mTimeCountHandler?.post(object : Runnable {
                override fun run() {
                    if (!isStartAlignment) {
                        mTime++
                        updateReadingProgressMsg()
                        try {
//                        progressPercent = ((mTime.toDouble() / Constant.BINAH_AI_SCANNING_TIME_SECONDS) * 100)
                            progressPercent = ((mTime.toDouble() / BuildConfig.SCANNING_TIME) * 100)

                            (binding as ActivityScanRBinding).readingProgressBar.setProgressPercentage(progressPercent, true)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    mTimeCountHandler?.postDelayed(this, 1000)
                }
            })
        }


    }

    private var isDetected = false
    private fun updateReadingProgressMsg() {
        if (isDetected && !isStartAlignment) {
            if (binding is ActivityScanBinding) {
                when (mTime) {
                    in 0..3 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.you_are_experiencing_rppg)
                    }

                    in 4..7 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.your_heart_beats_around)
                    }

                    in 8..11 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.our_tech_works_for_all_skin_tones)
                    }

                    in 12..15 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.you_actually_inhale_and_exhale)
                    }

                    in 16..19 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.stay_still_for_accurate_results)
                    }

                    in 20..23 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.rppg_imaging_is_proven)
                    }

                    in 24..27 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.not_just_old_folks)
                    }

                    in 28..31 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.stay_as_still_as_possible)
                    }

                    in 32..35 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.vital_signs_may_vary)
                    }

                    in 36..39 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.owning_a_pet_can)
                    }

                    in 40..43 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.high_blood_pressure_usually)
                    }

                    in 44..47 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.the_brain_uses)
                    }

                    in 48..51 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.squeezing_a_tennis_ball)
                    }

                    in 52..55 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.lifestyle_changes_are)
                    }

                    in 56..60 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.slow_breathing_improves)
                    }

                    in 61..100 -> {
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.text = getString(R.string.stay_as_still_as_possible)
                    }
                }
            } else if (binding is ActivityScanRBinding) {
                when (mTime) {
                    in 0..3 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.you_are_experiencing_rppg)
                    }

                    in 4..7 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.your_heart_beats_around)
                    }

                    in 8..11 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.our_tech_works_for_all_skin_tones)
                    }

                    in 12..15 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.you_actually_inhale_and_exhale)
                    }

                    in 16..19 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.stay_still_for_accurate_results)
                    }

                    in 20..23 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.rppg_imaging_is_proven)
                    }

                    in 24..27 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.not_just_old_folks)
                    }

                    in 28..31 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.stay_as_still_as_possible)
                    }

                    in 32..35 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.vital_signs_may_vary)
                    }

                    in 36..39 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.owning_a_pet_can)
                    }

                    in 40..43 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.high_blood_pressure_usually)
                    }

                    in 44..47 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.the_brain_uses)
                    }

                    in 48..51 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.squeezing_a_tennis_ball)
                    }

                    in 52..55 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.lifestyle_changes_are)
                    }

                    in 56..60 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.slow_breathing_improves)
                    }

                    in 61..100 -> {
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.text = getString(R.string.stay_as_still_as_possible)
                    }
                }
            }
        }
    }

    private fun stopTimeCount() {
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).readingProgressBar.visibility = View.GONE
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).readingProgressBar.visibility = View.GONE
        }
        mTimeCountHandler?.removeCallbacksAndMessages(null)
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_play_sound -> {

                if (Pref.isAudioOn) {
                    Pref.isAudioOn = false
                    stopPrompt()
                } else {
                    Pref.isAudioOn = true
                    playPrompt()
                }
                setPromptIcon()

//                try {
//                    if (mediaPlayer?.isPlaying == true) {
//                        mediaPlayer?.stop()
//                        mediaPlayer = null
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                mediaPlayer = MediaPlayer()
//                if (session?.state == SessionState.INITIALIZING || session?.state == SessionState.READY) {
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_instruction)
//                } else if (session?.state == SessionState.STARTING || session?.state == SessionState.PROCESSING) {
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_scan_start)
//                } else if (isErrorOrWarning) {
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_scanning_error)
//                } else {
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_instruction)
//                }
//                mediaPlayer?.start()
            }

            R.id.iv_back -> {
                finish()
            }

            R.id.iv_exit -> {
//                ExitActivity.startActivity(this)
                SplashActivity.startActivityClearTop(this)
//                ScanEndingActivity.startActivity(this, 0)
            }

            R.id.iv_low_view -> {
                Logger.e("session?.state = ${session?.state}")
                if (session == null || session?.state == SessionState.INITIALIZING || session?.state == SessionState.READY) {
                    Pref.isLowView = !Pref.isLowView
                    setCustomContentView()
                }
            }

            R.id.btn_start_alignment -> {
                val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    if (binding is ActivityScanBinding) {
                        (binding as ActivityScanBinding).clButtons.visibility = View.GONE
                    } else if (binding is ActivityScanRBinding) {
                        (binding as ActivityScanRBinding).clButtons.visibility = View.GONE
                    }

                    if (session?.state == SessionState.READY) {
                        try {
                            val time: Long = BuildConfig.SCANNING_TIME + 15
                            session?.start(time)
                            startAlignmentTimer()
                            startTimeCount()


                        } catch (e: HealthMonitorException) {
                            showWarning(BinahErrorMessage.getErrorMessage(e.errorCode))
//                                hideReadingAndProgress()
                        } catch (e: java.lang.IllegalStateException) {
                            showWarning("Start Error - Session in illegal state")
//                                hideReadingAndProgress()
                        } catch (e: NullPointerException) {
                            showWarning("Start Error - Session not initialized")
//                                hideReadingAndProgress()
                        }

                    }

                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
                }
            }

            R.id.btn_go_to_scan -> {
                val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    try {

                        if (session?.state == SessionState.READY) {
                            startMeasuring()
                        }
//                        if (session?.state == SessionState.PROCESSING) {
//                            isFromStop = true
//                            val request = ScanStatusRequest(scanStatus = Enums.ScanStatus.STOPPED.toString(), 0, getString(R.string.user_stopped_scan))
//                            onBoardingViewModel.scanStatus(request, Pref.generateCodeObject?.id!!)
//                        } else {
//                            if (session?.state == SessionState.READY) {
//                                startMeasuring()
//                            }
//                        }
                    } catch (e: HealthMonitorException) {
                        Utils.showErrorMessage(this, "" + e.errorCode)
                    }
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
                }
            }

            R.id.btn_scan -> {
                val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (session?.state == SessionState.PROCESSING) {
                            isFromStop = true
                            val request = ScanStatusRequest(scanStatus = Enums.ScanStatus.STOPPED.toString(), 0, getString(R.string.user_stopped_scan))
                            onBoardingViewModel.scanStatus(request, Pref.generateCodeObject?.id!!)
                        } else {
                            if (session?.state == SessionState.READY) {
//                                startMeasuring()
//                                onBoardingViewModel.beforeScan()
                            } else {
                                session?.stop()
                            }
                        }
                    } catch (e: HealthMonitorException) {
                        Utils.showErrorMessage(this, "" + e.errorCode)
                    }

                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
                }
//                isDetected = true
//                startTimeCount()
            }

        }
    }


    private fun startAlignmentTimer() {

//        (binding as ActivityScanBinding).llScanStartIn.visibility = View.VISIBLE
//        (binding as ActivityScanBinding).clScanInfo.visibility = View.INVISIBLE
//        (binding as ActivityScanBinding).clStartIn.visibility = View.VISIBLE
//        (binding as ActivityScanBinding).clScanProgress.visibility = View.INVISIBLE

        if (countDownTimer != null) {
            countDownTimer!!.cancel()
            countDownTimer = null
        }
        currentProgress = 0
        isStartAlignment = true
        countDownTimer = object : CountDownTimer((endTime * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setProgress(currentProgress, endTime)

                if (binding is ActivityScanBinding) {
                    (binding as ActivityScanBinding).tvRemainingTime.text = (endTime - currentProgress).toString()
                } else if (binding is ActivityScanRBinding) {
                    (binding as ActivityScanRBinding).tvRemainingTime.text = (endTime - currentProgress).toString()
                }
                currentProgress += 1
            }

            override fun onFinish() {
                isStartAlignment = false
                currentProgress = endTime
                if (binding is ActivityScanBinding) {
                    (binding as ActivityScanBinding).tvRemainingTime.text = (endTime - currentProgress).toString()
                } else if (binding is ActivityScanRBinding) {
                    (binding as ActivityScanRBinding).tvRemainingTime.text = (endTime - currentProgress).toString()
                }
                setProgress(currentProgress, endTime)

                Handler(Looper.getMainLooper()).postDelayed({
                    if (binding is ActivityScanBinding) {
                        (binding as ActivityScanBinding).lblScanStarted.visibility = View.GONE
                    } else if (binding is ActivityScanRBinding) {
                        (binding as ActivityScanRBinding).lblScanStarted.visibility = View.GONE
                    }
                }, 5000)

//                (binding as ActivityScanBinding).llScanStartIn.visibility = View.GONE


//                Handler(Looper.getMainLooper()).postDelayed({
//                    isStartAlignment = false
//                    if (session?.state == SessionState.READY) {
//                        startMeasuring()
//                    }
//                }, 2000)

            }
        }

        countDownTimer!!.start()
    }

    fun setProgress(currentProgress: Int, endTime: Int) {
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).cpStartIn.max = endTime
            (binding as ActivityScanBinding).cpStartIn.secondaryProgress = endTime
            (binding as ActivityScanBinding).cpStartIn.progress = currentProgress
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).cpStartIn.max = endTime
            (binding as ActivityScanRBinding).cpStartIn.secondaryProgress = endTime
            (binding as ActivityScanRBinding).cpStartIn.progress = currentProgress
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createSession()
        }
    }

    private var isFirstTime = true
    override fun onResume() {
        super.onResume()
        if (handlerPublishReport == null && isResultPublishedTimePassed) {
            showResultScreen(faceResultID)
        }
        setCustomContentView()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_scan_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_scan)
        }
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)

        if (binding is ActivityScanBinding) {
            Glide.with(this).load(R.raw.ic_camera_dark_top4).into((binding as ActivityScanBinding).ivCameraDark)
        } else if (binding is ActivityScanRBinding) {
            Glide.with(this).load(R.raw.ic_camera_dark_top4).into((binding as ActivityScanRBinding).ivCameraDark)
        }


        initClick()
        initSpan()
        setValues()
        setViewBeforeScanning()
        setPromptIcon()

//        if (isFirstTime) {
//            showHideProgress(true)
//            Handler(Looper.getMainLooper()).postDelayed({
//                showHideProgress(false)
//                isFirstTime = false
//            }, 200)
//        }


//        (binding as ActivityScanBinding).cameraView.post(Runnable {
//            val width = (binding as ActivityScanBinding).cameraView.width
//            val height = (binding as ActivityScanBinding).cameraView.height
//            Logger.e("width = $width  height = $height")
//
//
//            val x = (binding as ActivityScanBinding).cameraView.x
//            val y = (binding as ActivityScanBinding).cameraView.y
//
//            val location = IntArray(2)
//            (binding as ActivityScanBinding).cameraView.getLocationOnScreen(location)
//            Logger.e("location0 = ${location[0]}  location1 = $${location[1]}")
//
//        })
    }

    override fun onStart() {
        super.onStart()

        if (isFirstTime) {
            showHideProgress(true)
            Handler(Looper.getMainLooper()).postDelayed({
                showHideProgress(false)
                isFirstTime = false
            }, 200)
        }

        val permissionStatus = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
//            createSession()
            Handler(Looper.getMainLooper()).postDelayed({
                createSession()
            }, 100)
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), permissionsRequestCode)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            session?.terminate()
            session = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

        stopPrompt()

        try {
            if (countDownTimer != null) {
                countDownTimer!!.cancel()
                countDownTimer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        stopTimeOut1()
    }

    private fun showResultScreen(id: Long) {
        run {
            isResultPublishedTimePassed = false
        }
    }

    private fun createSession() {
        val licenseDetails = LicenseDetails(BuildConfig.LICENCE_KEY)
        try {
            session = FaceSessionBuilder(applicationContext).withImageListener(this).withVitalSignsListener(this).withSessionInfoListener(this).build(licenseDetails)
        } catch (e: HealthMonitorException) {
            e.printStackTrace()
        }
    }

    private fun startMeasuring() {
        try {
            session?.start(BuildConfig.SCANNING_TIME.toLong())
            startTimeCount()

            Handler(Looper.getMainLooper()).postDelayed({
                if (binding is ActivityScanBinding) {
                    (binding as ActivityScanBinding).lblScanStarted.visibility = View.GONE
                } else if (binding is ActivityScanRBinding) {
                    (binding as ActivityScanRBinding).lblScanStarted.visibility = View.GONE
                }
            }, 5000)


//            val matrix = Matrix()
//            matrix.preScale(-1f, 1f)
//            matrix.postTranslate((binding as ActivityScanBinding).ivCameraViewAnim.width.toFloat(), 0f)
//            (binding as ActivityScanBinding).ivCameraViewAnim.imageMatrix = matrix
//            Glide.with(this).load(R.drawable.ic_scanning_bg).into((binding as ActivityScanBinding).ivCameraViewAnim)

        } catch (e: HealthMonitorException) {
            showWarning(BinahErrorMessage.getErrorMessage(e.errorCode))
            hideReadingAndProgress()
        } catch (e: java.lang.IllegalStateException) {
            showWarning("Start Error - Session in illegal state")
            hideReadingAndProgress()
        } catch (e: NullPointerException) {
            showWarning("Start Error - Session not initialized")
            hideReadingAndProgress()
        }
    }

    private fun stopMeasuring() {
        try {
            if (session != null) {
                session?.stop()
            }
        } catch (e: IllegalStateException) {
//            showWarning("Stop Error - Session in illegal state")
            e.printStackTrace()
        } catch (ignore: NullPointerException) {
            ignore.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeSession() {
        if (session != null) {
            session?.terminate()
        }
        session = null
    }


    private fun setViewBeforeScanning() {
        if (binding is ActivityScanBinding) {

//            (binding as ActivityScanBinding).clReadyToBegin.visibility = View.VISIBLE
//            (binding as ActivityScanBinding).clScanningInProgress.visibility = View.GONE
            (binding as ActivityScanBinding).clScanInfo.visibility = View.VISIBLE
            (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).clScanProgress.visibility = View.INVISIBLE

            (binding as ActivityScanBinding).clButtons.visibility = View.VISIBLE
            (binding as ActivityScanBinding).btnScan.visibility = View.INVISIBLE

            (binding as ActivityScanBinding).tvScanningError.visibility = View.GONE
            (binding as ActivityScanBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).ivVitalSignScan.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).readingProgressBar.visibility = View.INVISIBLE

//            (binding as ActivityScanBinding).lblRemainAsStill.visibility = View.VISIBLE
//            (binding as ActivityScanBinding).tvReadyToBegin.visibility = View.VISIBLE

//            (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
//            (binding as ActivityScanBinding).llScanStartIn.visibility = View.GONE
//            (binding as ActivityScanBinding).tvScanMessage.visibility = View.GONE
            (binding as ActivityScanBinding).tvScanReading.visibility = View.GONE
            (binding as ActivityScanBinding).tvScanReadingUnit.visibility = View.GONE

            (binding as ActivityScanBinding).ivCameraView.visibility = View.VISIBLE
//            (binding as ActivityScanBinding).ivCameraView.setImageResource(R.drawable.face)


        } else if (binding is ActivityScanRBinding) {

//            (binding as ActivityScanRBinding).clReadyToBegin.visibility = View.VISIBLE
//            (binding as ActivityScanRBinding).clScanningInProgress.visibility = View.GONE

            (binding as ActivityScanRBinding).clScanInfo.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).clStartIn.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).clScanProgress.visibility = View.INVISIBLE

            (binding as ActivityScanRBinding).clButtons.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).btnScan.visibility = View.INVISIBLE

            (binding as ActivityScanRBinding).tvScanningError.visibility = View.GONE
            (binding as ActivityScanRBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).ivVitalSignScan.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).readingProgressBar.visibility = View.INVISIBLE

//            (binding as ActivityScanRBinding).lblRemainAsStill.visibility = View.VISIBLE
//            (binding as ActivityScanRBinding).tvReadyToBegin.visibility = View.VISIBLE


//            (binding as ActivityScanRBinding).llScanReading.visibility = View.GONE
//            (binding as ActivityScanRBinding).tvScanMessage.visibility = View.GONE
            (binding as ActivityScanRBinding).tvScanReading.visibility = View.GONE
            (binding as ActivityScanRBinding).tvScanReadingUnit.visibility = View.GONE

            (binding as ActivityScanRBinding).ivCameraView.visibility = View.VISIBLE
//            (binding as ActivityScanRBinding).ivCameraView.setImageResource(R.drawable.face)
        }
    }


    private fun hideReadingAndProgress() {
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).tvScanReading.visibility = View.GONE
            (binding as ActivityScanBinding).tvScanReadingUnit.visibility = View.GONE
            (binding as ActivityScanBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).ivVitalSignScan.visibility = View.INVISIBLE
            (binding as ActivityScanBinding).readingProgressBar.visibility = View.INVISIBLE

//            (binding as ActivityScanBinding).lblRemainAsStill.visibility = View.VISIBLE
//            (binding as ActivityScanBinding).tvReadyToBegin.visibility = View.VISIBLE

        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).tvScanReading.visibility = View.GONE
            (binding as ActivityScanRBinding).tvScanReadingUnit.visibility = View.GONE

            (binding as ActivityScanRBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).ivVitalSignScan.visibility = View.INVISIBLE
            (binding as ActivityScanRBinding).readingProgressBar.visibility = View.INVISIBLE

//            (binding as ActivityScanRBinding).lblRemainAsStill.visibility = View.VISIBLE
//            (binding as ActivityScanRBinding).tvReadyToBegin.visibility = View.VISIBLE
        }
    }

    private fun showWarning(text: String) {
        showWarning(text, null)
    }

    private fun showWarning(text1: String, errorCode: Int?) {
        var text: String? = text1
        if (errorCode != null) {
            text += " ($errorCode)"
        }
        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).tvScanningError.visibility = View.VISIBLE
            (binding as ActivityScanBinding).tvScanningError.text = text
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).tvScanningError.visibility = View.VISIBLE
            (binding as ActivityScanRBinding).tvScanningError.text = text
        }

    }

//    private fun getMapIcon(): BitmapDescriptor? {
//        val resourceId: Int = R.drawable.myimage
//        return BitmapDescriptorFactory.fromResource(resourceId)
//    }

    override fun onImage(imageData: ImageData) {

        runOnUiThread {
//            Toast.makeText(this, "ON IMAGE ${imageData.imageValidity}", Toast.LENGTH_SHORT).show()
//            handleImage(imageData.image, imageData.roi)
//            handleRoiDetection(imageData.imageValidity)

            isDetected = imageData.imageValidity == ImageValidity.VALID
//            Logger.e("isDetected in onImage == $isDetected")

            if (binding is ActivityScanBinding) {
                (binding as ActivityScanBinding).cameraView.lockCanvas()?.let { canvas ->
                    // Drawing the bitmap on the TextureView canvas

                    flipCanvas(canvas)
                    val image = imageData.image
                    canvas.drawBitmap(
                        image,
                        null,
                        Rect(0, 0, (binding as ActivityScanBinding).cameraView.width, (binding as ActivityScanBinding).cameraView.bottom - (binding as ActivityScanBinding).cameraView.top),
                        null
                    )

                    //Drawing the face detection (if not null..)
                    imageData.roi?.let roi@{ faceDetectionRect ->
                        //First we scale the SDK face detection rectangle to fit the TextureView size

                        val targetRect = RectF(faceDetectionRect)

                        val m = Matrix()
                        m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                        m.postScale(
                            (binding as ActivityScanBinding).cameraView.width.toFloat() / image.width.toFloat(), (binding as ActivityScanBinding).cameraView.height.toFloat() / image.height.toFloat()
                        )
                        m.mapRect(targetRect)
                        canvas.drawBitmap(faceDetection ?: return@roi, null, targetRect, null)
                    }

                    (binding as ActivityScanBinding).cameraView.unlockCanvasAndPost(canvas)
                }
            } else if (binding is ActivityScanRBinding) {
                (binding as ActivityScanRBinding).cameraView.lockCanvas()?.let { canvas ->
                    // Drawing the bitmap on the TextureView canvas

                    flipCanvas(canvas)

                    val image = imageData.image
                    canvas.drawBitmap(
                        image,
                        null,
                        Rect(0, 0, (binding as ActivityScanRBinding).cameraView.width, (binding as ActivityScanRBinding).cameraView.bottom - (binding as ActivityScanRBinding).cameraView.top),
                        null
                    )

                    //Drawing the face detection (if not null..)
                    imageData.roi?.let roi@{ faceDetectionRect ->
                        //First we scale the SDK face detection rectangle to fit the TextureView size
                        val targetRect = RectF(faceDetectionRect)

                        val m = Matrix()
                        m.postScale(1f, 1f, image.width / 2f, image.height / 2f)
                        m.postScale(
                            (binding as ActivityScanRBinding).cameraView.width.toFloat() / image.width.toFloat(), (binding as ActivityScanRBinding).cameraView.height.toFloat() / image.height.toFloat()
                        )
                        m.mapRect(targetRect)
                        // Then we draw it on the Canvas
                        canvas.drawBitmap(faceDetection ?: return@roi, null, targetRect, null)
                    }
                    (binding as ActivityScanRBinding).cameraView.unlockCanvasAndPost(canvas)
                }
            }


            if (session?.state == SessionState.STARTING || session?.state == SessionState.PROCESSING) {
                if (isDetected) {
                    updateReadingProgressMsg()
                    if (binding is ActivityScanBinding) {

                        (binding as ActivityScanBinding).clScanInfo.visibility = View.INVISIBLE
//                        (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
//                        (binding as ActivityScanBinding).clScanProgress.visibility = View.VISIBLE

//                        (binding as ActivityScanBinding).clReadyToBegin.visibility = View.GONE
//                        (binding as ActivityScanBinding).clScanningInProgress.visibility = View.VISIBLE

                        if (isStartAlignment) {
                            (binding as ActivityScanBinding).clStartIn.visibility = View.VISIBLE
                            (binding as ActivityScanBinding).clScanProgress.visibility = View.INVISIBLE
//                            (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
//                            (binding as ActivityScanBinding).llScanStartIn.visibility = View.VISIBLE
                            (binding as ActivityScanBinding).btnScan.visibility = View.INVISIBLE
                        } else {
                            (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
                            (binding as ActivityScanBinding).clScanProgress.visibility = View.VISIBLE
//                            (binding as ActivityScanBinding).llScanReading.visibility = View.VISIBLE
//                            (binding as ActivityScanBinding).llScanStartIn.visibility = View.GONE
                            (binding as ActivityScanBinding).btnScan.visibility = View.VISIBLE
                        }


                        (binding as ActivityScanBinding).tvScanningError.visibility = View.GONE
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.visibility = View.VISIBLE
                        (binding as ActivityScanBinding).ivVitalSignScan.visibility = View.VISIBLE
                        (binding as ActivityScanBinding).readingProgressBar.visibility = View.VISIBLE

//                        (binding as ActivityScanBinding).tvScanMessage.visibility = View.VISIBLE
                        (binding as ActivityScanBinding).tvScanReading.visibility = View.VISIBLE
                        (binding as ActivityScanBinding).tvScanReadingUnit.visibility = View.VISIBLE

//                        (binding as ActivityScanBinding).lblRemainAsStill.visibility = View.GONE
//                        (binding as ActivityScanBinding).tvReadyToBegin.visibility = View.GONE

                    } else if (binding is ActivityScanRBinding) {
//                        (binding as ActivityScanRBinding).clReadyToBegin.visibility = View.GONE
//                        (binding as ActivityScanRBinding).clScanningInProgress.visibility = View.VISIBLE


                        if (isStartAlignment) {
                            (binding as ActivityScanRBinding).clStartIn.visibility = View.VISIBLE
                            (binding as ActivityScanRBinding).clScanProgress.visibility = View.INVISIBLE
//                            (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
//                            (binding as ActivityScanBinding).llScanStartIn.visibility = View.VISIBLE
                            (binding as ActivityScanRBinding).btnScan.visibility = View.INVISIBLE
                        } else {
                            (binding as ActivityScanRBinding).clStartIn.visibility = View.INVISIBLE
                            (binding as ActivityScanRBinding).clScanProgress.visibility = View.VISIBLE
//                            (binding as ActivityScanBinding).llScanReading.visibility = View.VISIBLE
//                            (binding as ActivityScanBinding).llScanStartIn.visibility = View.GONE
                            (binding as ActivityScanRBinding).btnScan.visibility = View.VISIBLE
                        }


                        (binding as ActivityScanRBinding).tvScanningError.visibility = View.GONE
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.visibility = View.VISIBLE
                        (binding as ActivityScanRBinding).ivVitalSignScan.visibility = View.VISIBLE
                        (binding as ActivityScanRBinding).readingProgressBar.visibility = View.VISIBLE
//                        (binding as ActivityScanRBinding).llScanReading.visibility = View.VISIBLE
//                        (binding as ActivityScanRBinding).tvScanMessage.visibility = View.VISIBLE
                        (binding as ActivityScanRBinding).tvScanReading.visibility = View.VISIBLE
                        (binding as ActivityScanRBinding).tvScanReadingUnit.visibility = View.VISIBLE

//                        (binding as ActivityScanRBinding).lblRemainAsStill.visibility = View.GONE
//                        (binding as ActivityScanRBinding).tvReadyToBegin.visibility = View.GONE
                    }

                } else {

                    var msg = getString(R.string.face_not_detected) + "\n" + getString(R.string.face_not_detected_msg)
                    if (binding is ActivityScanBinding) {
                        setSpannableColor(
                            (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.face_not_detected), ContextCompat.getColor(this, R.color.white)
                        )

                        when (imageData.imageValidity) {
                            ImageValidity.INVALID_DEVICE_ORIENTATION -> {
                                msg = getString(R.string.unsupported_orientation)
//                                msg = getString(R.string.unsupported_orientation) + "\n" + getString(R.string.unsupported_orientation_msg)
                                setSpannableColor(
                                    (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.unsupported_orientation), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.INVALID_ROI -> {
//                                msg = getString(R.string.face_not_detected) + "\n" + getString(R.string.face_not_detected_msg)
                                msg = getString(R.string.align_your_face_to_the_frame)
                                setSpannableColor(
                                    (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.align_your_face_to_the_frame), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.TILTED_HEAD -> {
//                                msg = getString(R.string.tilted_head) + "\n" + getString(R.string.tilted_head_msg)
                                msg = getString(R.string.straighten_your_head)
                                setSpannableColor(
                                    (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.straighten_your_head), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.FACE_TOO_FAR -> {
//                                msg = getString(R.string.face_to_far) + "\n" + getString(R.string.face_to_far_msg)
                                msg = getString(R.string.come_closer)
                                setSpannableColor(
                                    (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.come_closer), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.UNEVEN_LIGHT -> {
//                                msg = getString(R.string.unenven_lighting) + "\n" + getString(R.string.unenven_lighting_msg)
                                msg = getString(R.string.eliminate_shadow_or_glare)
                                setSpannableColor(
                                    (binding as ActivityScanBinding).tvScanningError, msg, getString(R.string.eliminate_shadow_or_glare), ContextCompat.getColor(this, R.color.white)
                                )
                            }
                        }

//                        (binding as ActivityScanBinding).clReadyToBegin.visibility = View.GONE
//                        (binding as ActivityScanBinding).clScanningInProgress.visibility = View.GONE

                        (binding as ActivityScanBinding).clScanInfo.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).clScanProgress.visibility = View.INVISIBLE

                        (binding as ActivityScanBinding).tvScanningError.visibility = View.VISIBLE
                        (binding as ActivityScanBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).ivVitalSignScan.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).readingProgressBar.visibility = View.INVISIBLE
//                        (binding as ActivityScanBinding).llScanReading.visibility = View.GONE
//                        (binding as ActivityScanBinding).tvScanMessage.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).tvScanReading.visibility = View.INVISIBLE
                        (binding as ActivityScanBinding).tvScanReadingUnit.visibility = View.INVISIBLE

//                        (binding as ActivityScanBinding).lblRemainAsStill.visibility = View.VISIBLE
//                        (binding as ActivityScanBinding).tvReadyToBegin.visibility = View.VISIBLE

                    } else if (binding is ActivityScanRBinding) {
                        setSpannableColor(
                            (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.face_not_detected), ContextCompat.getColor(this, R.color.white)
                        )

                        when (imageData.imageValidity) {
                            ImageValidity.INVALID_DEVICE_ORIENTATION -> {
//                                msg = getString(R.string.unsupported_orientation) + "\n" + getString(R.string.unsupported_orientation_msg)
                                msg = getString(R.string.unsupported_orientation)
                                setSpannableColor(
                                    (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.unsupported_orientation), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.INVALID_ROI -> {
//                                msg = getString(R.string.face_not_detected) + "\n" + getString(R.string.face_not_detected_msg)
                                msg = getString(R.string.align_your_face_to_the_frame)
                                setSpannableColor(
                                    (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.align_your_face_to_the_frame), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.TILTED_HEAD -> {
//                                msg = getString(R.string.tilted_head) + "\n" + getString(R.string.tilted_head_msg)
                                msg = getString(R.string.straighten_your_head)
                                setSpannableColor(
                                    (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.straighten_your_head), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.FACE_TOO_FAR -> {
//                                msg = getString(R.string.face_to_far) + "\n" + getString(R.string.face_to_far_msg)
                                msg = getString(R.string.come_closer)
                                setSpannableColor(
                                    (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.come_closer), ContextCompat.getColor(this, R.color.white)
                                )
                            }

                            ImageValidity.UNEVEN_LIGHT -> {
//                                msg = getString(R.string.unenven_lighting) + "\n" + getString(R.string.unenven_lighting_msg)
                                msg = getString(R.string.eliminate_shadow_or_glare)
                                setSpannableColor(
                                    (binding as ActivityScanRBinding).tvScanningError, msg, getString(R.string.eliminate_shadow_or_glare), ContextCompat.getColor(this, R.color.white)
                                )
                            }
                        }

//                        (binding as ActivityScanRBinding).clReadyToBegin.visibility = View.GONE
//                        (binding as ActivityScanRBinding).clScanningInProgress.visibility = View.GONE

                        (binding as ActivityScanRBinding).clScanInfo.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).clStartIn.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).clScanProgress.visibility = View.INVISIBLE

                        (binding as ActivityScanRBinding).tvScanningError.visibility = View.VISIBLE
                        (binding as ActivityScanRBinding).tvVitalSignScanMessage.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).ivVitalSignScan.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).readingProgressBar.visibility = View.INVISIBLE
//                        (binding as ActivityScanRBinding).llScanReading.visibility = View.GONE
//                        (binding as ActivityScanRBinding).tvScanMessage.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).tvScanReading.visibility = View.INVISIBLE
                        (binding as ActivityScanRBinding).tvScanReadingUnit.visibility = View.INVISIBLE

//                        (binding as ActivityScanRBinding).lblRemainAsStill.visibility = View.VISIBLE
//                        (binding as ActivityScanRBinding).tvReadyToBegin.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    private fun flipCanvas(canvas: Canvas) {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        matrix.postTranslate(canvas.width.toFloat(), 0f)
        canvas.setMatrix(matrix)
    }

    private fun setSpannableColor(view: TextView, fulltext: String, subtext1: String, color: Int) {
        val str: Spannable = SpannableString(fulltext)
        val i1 = fulltext.indexOf(subtext1)
        str.setSpan(
            ForegroundColorSpan(color), i1, i1 + subtext1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        str.setSpan(
            AbsoluteSizeSpan(14, true), i1 + subtext1.length, fulltext.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        view.movementMethod = LinkMovementMethod.getInstance()
        view.text = str
        view.highlightColor = Color.TRANSPARENT
    }


    override fun onVitalSign(vitalSign: VitalSign?) {
        runOnUiThread {
            isErrorOrWarning = false
            Log.i(TAG, "onVitalSign Message Type: " + vitalSign?.type + " message: " + vitalSign?.value)
            when (vitalSign?.type) {
                VitalSignTypes.PULSE_RATE -> {
                    val pulseRate = (vitalSign as VitalSignPulseRate).value
                    if (binding is ActivityScanBinding) {
                        (binding as ActivityScanBinding).tvScanReading.text = pulseRate?.toString() ?: "--"
                    } else if (binding is ActivityScanRBinding) {
                        (binding as ActivityScanRBinding).tvScanReading.text = pulseRate?.toString() ?: "--"
                    }
//                    Toast.makeText(this, "ON VITAL SIGN ${vitalSign?.type} -> ${vitalSign?.value} ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onFinalResults(finalResults: VitalSignsResults) {
        runOnUiThread {
            isErrorOrWarning = false
            handleFinalResults(finalResults)
        }
    }

    override fun onSessionStateChange(sessionState: SessionState) {

        Log.e("sessionState", "${sessionState?.name}")
        runOnUiThread {
            when (sessionState) {

                SessionState.INITIALIZING -> {
                    clearCanvas()
                }

                SessionState.READY -> {
//                    binding.startStopButton.isEnabled = true
//                    binding.startStopButton.text = getString(R.string.start)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                SessionState.PROCESSING -> {
//                    binding.startStopButton.isEnabled = true
//                    binding.startStopButton.text = getString(R.string.stop)
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                SessionState.STOPPING -> {
//                    stopMeasuring()
//                    stopTimeCount()
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }

                else -> {
//                    binding.startStopButton.isEnabled = false
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }
        }
    }

    private fun clearCanvas() {
        if (binding is ActivityScanBinding) {
            val canvas: Canvas? = (binding as ActivityScanBinding).cameraView.lockCanvas()
            canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
            if (canvas != null) (binding as ActivityScanBinding).cameraView.unlockCanvasAndPost(canvas)
        } else if (binding is ActivityScanRBinding) {
            val canvas: Canvas? = (binding as ActivityScanRBinding).cameraView.lockCanvas()
            canvas?.drawColor(ContextCompat.getColor(baseContext, R.color.colorScreenBackground))
            if (canvas != null) (binding as ActivityScanRBinding).cameraView.unlockCanvasAndPost(canvas)
        }


    }


    override fun onWarning(warningData: WarningData) {
        runOnUiThread {
            isErrorOrWarning = true
//            Toast.makeText(this, "ON WARNING ${warningData.code}", Toast.LENGTH_SHORT).show()
            when (warningData.code) {
                AlertCodes.INITIALIZATION_CODE_ROTATION_AND_ORIENTATION_MISMATCH -> {
                    if (session != null && session?.state == SessionState.PROCESSING) {
                        showWarning("" + warningData.code)
                    }
                }

                AlertCodes.MEASUREMENT_CODE_MISDETECTION_DURATION_EXCEEDS_LIMIT_WARNING -> {
                    resetMeasurements()
                    showWarning("" + warningData.code)
                }

                else -> showWarning("" + warningData.code)
            }
        }
    }

    private fun resetMeasurements() {

        if (binding is ActivityScanBinding) {
            (binding as ActivityScanBinding).tvScanReading.text = ""
        } else if (binding is ActivityScanRBinding) {
            (binding as ActivityScanRBinding).tvScanReading.text = ""
        }

//            if (mLicenseEnabledVitalSigns.isHeartRateEnabled) "--" else getString(R.string.na)
    }


    override fun onError(errorData: ErrorData) {
        runOnUiThread {
            try {
                if (countDownTimer != null) {
                    countDownTimer!!.cancel()
                    countDownTimer = null
                }
                isStartAlignment = false
            } catch (e: Exception) {
                e.printStackTrace()
            }


//            Toast.makeText(this, "ON ERROR ${errorData.code}", Toast.LENGTH_SHORT).show()
            try {
                Logger.e("onError == onError Called ${errorData.code}")

//                Toast.makeText(this, "${BinahErrorMessage.getErrorMessage(errorData.code)}", Toast.LENGTH_SHORT).show()

//                showWarning(getString(R.string.error_warning), errorData.code)

                isErrorOrWarning = true
                resetMeasurements()
                setViewBeforeScanning()
//                stopMeasuring()

                var errorMessage = BinahErrorMessage.getErrorMessage(errorData.code)
                if (errorMessage.trim().isNullOrEmpty()) {
                    errorMessage = errorData.code.toString()
                }

                if (binding is ActivityScanBinding) {

//                    (binding as ActivityScanBinding).clReadyToBegin.visibility = View.GONE
//                    (binding as ActivityScanBinding).clScanningInProgress.visibility = View.GONE

                    (binding as ActivityScanBinding).clScanInfo.visibility = View.INVISIBLE
                    (binding as ActivityScanBinding).clStartIn.visibility = View.INVISIBLE
                    (binding as ActivityScanBinding).clScanProgress.visibility = View.INVISIBLE

                    (binding as ActivityScanBinding).tvScanningError.visibility = View.VISIBLE
                    (binding as ActivityScanBinding).tvScanningError.text = errorMessage
                } else if (binding is ActivityScanRBinding) {
//                    (binding as ActivityScanRBinding).clReadyToBegin.visibility = View.GONE
//                    (binding as ActivityScanRBinding).clScanningInProgress.visibility = View.GONE

                    (binding as ActivityScanRBinding).clScanInfo.visibility = View.INVISIBLE
                    (binding as ActivityScanRBinding).clStartIn.visibility = View.INVISIBLE
                    (binding as ActivityScanRBinding).clScanProgress.visibility = View.INVISIBLE

                    (binding as ActivityScanRBinding).tvScanningError.visibility = View.VISIBLE
                    (binding as ActivityScanRBinding).tvScanningError.text = errorMessage
                }

                isFromStop = false
                val request = ScanStatusRequest(scanStatus = Enums.ScanStatus.FAILED.toString(), errorData.code, errorMessage)
                onBoardingViewModel.scanStatus(request, Pref.generateCodeObject?.id!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onLicenseInfo(licenseInfo: LicenseInfo) {
        runOnUiThread {
            licenseInfo.licenseActivationInfo.activationID.takeIf { it.isNotEmpty() }?.let { id ->
                try {
                    Log.i(TAG, "License Activation ID: $id")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            licenseInfo.licenseOfflineMeasurements?.let { offlineMeasurements ->
                try {
                    Log.i(
                        TAG, "License Offline Measurements: " + "${offlineMeasurements.totalMeasurements}/" + "${offlineMeasurements.remainingMeasurements}"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onEnabledVitalSigns(sessionEnabledVitalSigns: SessionEnabledVitalSigns) {
        runOnUiThread {
            enabledVitalSigns = sessionEnabledVitalSigns
            Log.i(TAG, "Pulse Rate Enabled: ${sessionEnabledVitalSigns.isEnabled(VitalSignTypes.PULSE_RATE)}")
        }
    }


    @SuppressLint("SetTextI18n")
    private fun handleFinalResults(finalResults: VitalSignsResults) {

//        val enabledVitalSigns = resolveEnabledVitalSigns()
        if (enabledVitalSigns?.isEnabled(VitalSignTypes.PULSE_RATE) == true) {
            val bpm = finalResults.getResult(VitalSignTypes.PULSE_RATE)
            if (binding is ActivityScanBinding) {
                if (bpm?.value == null) {
                    (binding as ActivityScanBinding).tvScanReading.text = "-"
                } else {
                    (binding as ActivityScanBinding).tvScanReading.text = "" + bpm.value
                }
            } else if (binding is ActivityScanRBinding) {
                if (bpm?.value == null) {
                    (binding as ActivityScanRBinding).tvScanReading.text = "-"
                } else {
                    (binding as ActivityScanRBinding).tvScanReading.text = "" + bpm.value
                }
            }

        }


        val scanningResultData = ScanningResultData()

        Logger.e("finalResults == ${finalResults.results.toString()}")

        Log.d(
            "Final Result = HEART_RATE ", "" + finalResults.getResult(VitalSignTypes.PULSE_RATE)?.value
        )
        Log.d(
            "Final Result = OXYGEN_SATURATION", "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value
        )

        if (enabledVitalSigns?.isEnabled(VitalSignTypes.HEMOGLOBIN) == true) {
            Logger.e("HEMOGLOBIN isEnabled true")
        } else {
            Logger.e("HEMOGLOBIN isEnabled false")
        }

        Log.d(
            "Final Result = HEMOGLOBIN ", "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value
        )
        Log.d(
            "Final Result = HEMOGLOBIN_A1C ", "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value
        )
        Log.d("Final Result = SDNN ", "" + finalResults.getResult(VitalSignTypes.SDNN)?.value)
        Log.d(
            "Final Result = STRESS_LEVEL ", "" + finalResults.getResult(VitalSignTypes.STRESS_LEVEL)?.value
        )
        Log.d(
            "Final Result = BLOOD_PRESSURE", "" + (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure?)?.value?.systolic + "/" + (finalResults.getResult(
                VitalSignTypes.BLOOD_PRESSURE
            ) as VitalSignBloodPressure?)?.value?.systolic
        )
        Log.d("Final Result = PRQ ", "" + finalResults.getResult(VitalSignTypes.PRQ)?.value)
        Log.d(
            "Final Result = BREATHING_RATE ", "" + finalResults.getResult(VitalSignTypes.RESPIRATION_RATE)?.value
        )
        Log.d(
            "Final Result = WELLNESS_INDEX ", "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value
        )
        Log.d(
            "Final Result = SNS_ZONE ", "" + finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value
        )
        Log.d(
            "Final Result = PNS_ZONE ", "" + finalResults.getResult(VitalSignTypes.PNS_ZONE)?.value
        )

        if (finalResults.getResult(VitalSignTypes.PULSE_RATE)?.value == null) {
            scanningResultData.heartRate = "0"
        } else {
            scanningResultData.heartRate = "" + finalResults.getResult(VitalSignTypes.PULSE_RATE).value
        }

        var pulseRateConfidence: String? = ""
        var pulseRateConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.name != null) {
            pulseRateConfidence = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal != null) {
            pulseRateConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.PULSE_RATE) as? VitalSignPulseRate)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("pulseRateConfidence = $pulseRateConfidence")
        Logger.e("pulseRateConfidenceOrdinal = $pulseRateConfidenceOrdinal")
        scanningResultData.heartRateConfLevel = pulseRateConfidenceOrdinal

        if (finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION)?.value == null) {
            scanningResultData.oxygenSaturation = "0"
        } else {
            scanningResultData.oxygenSaturation = "" + finalResults.getResult(VitalSignTypes.OXYGEN_SATURATION).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN)?.value == null) {
            scanningResultData.hemoglobin = "0"
        } else {
            scanningResultData.hemoglobin = "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN).value
        }

        if (finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C)?.value == null) {
            scanningResultData.hba1c = "0"
        } else {
            scanningResultData.hba1c = "" + finalResults.getResult(VitalSignTypes.HEMOGLOBIN_A1C).value
        }

        if (finalResults.getResult(VitalSignTypes.SDNN)?.value == null) {
            scanningResultData.hrvSdnn = "0"
        } else {
            scanningResultData.hrvSdnn = "" + finalResults.getResult(VitalSignTypes.SDNN).value
        }


        var sdnnConfidence: String? = ""
        var sdnnConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.name != null) {
            sdnnConfidence = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal != null) {
            sdnnConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.SDNN) as? VitalSignSDNN)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("sdnnConfidence = $sdnnConfidence")
        Logger.e("sdnnConfidenceOrdinal = $sdnnConfidenceOrdinal")
        scanningResultData.hrvSdnnConfLevel = sdnnConfidenceOrdinal

        if ((finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value == null || (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel?)?.value?.ordinal == 0) {
            scanningResultData.stressLevel = 0
        } else {
            scanningResultData.stressLevel = (finalResults.getResult(VitalSignTypes.STRESS_LEVEL) as VitalSignStressLevel).value.ordinal
        }

        if (finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE)?.value == null) {
            scanningResultData.bloodPressure = "0"
        } else {
            val bloodPressureValue = finalResults.getResult(VitalSignTypes.BLOOD_PRESSURE) as VitalSignBloodPressure
            scanningResultData.bloodPressure = "" + bloodPressureValue.value.systolic + "/" + bloodPressureValue.value.diastolic
        }
        if (finalResults.getResult(VitalSignTypes.PRQ)?.value == null) {
            scanningResultData.prq = "0"
        } else {
            scanningResultData.prq = "" + finalResults.getResult(VitalSignTypes.PRQ).value
        }

        var prqConfidence: String? = ""
        var prqConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.name != null) {
            prqConfidence = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal != null) {
            prqConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.PRQ) as? VitalSignPRQ)?.confidence?.level?.ordinal?.toString()
        }
        Logger.e("prqConfidence = $prqConfidence")
        Logger.e("prqConfidenceOrdinal = $prqConfidenceOrdinal")
        scanningResultData.prqConfLevel = prqConfidenceOrdinal


        if (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE)?.value == null) {
            scanningResultData.breathingRate = ""
        } else {
            scanningResultData.breathingRate = "" + finalResults.getResult(VitalSignTypes.RESPIRATION_RATE).value
        }

        var respirationRateConfidence: String? = ""
        var respirationRateConfidenceOrdinal: String? = ""
        if ((finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.name != null) {
            respirationRateConfidence = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.name
        }
        if ((finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal != null) {
            respirationRateConfidenceOrdinal = (finalResults.getResult(VitalSignTypes.RESPIRATION_RATE) as? VitalSignRespirationRate)?.confidence?.level?.ordinal.toString()
        }
        Logger.e("respirationRateConfidence = $respirationRateConfidence")
        Logger.e("respirationRateConfidenceOrdinal = $respirationRateConfidenceOrdinal")
        scanningResultData.breathingRateConfLevel = respirationRateConfidenceOrdinal

        if (finalResults.getResult(VitalSignTypes.WELLNESS_INDEX)?.value == null) {
            scanningResultData.wellnessScore = ""
        } else {
            scanningResultData.wellnessScore = "" + finalResults.getResult(VitalSignTypes.WELLNESS_INDEX).value
        }

        if (finalResults.getResult(VitalSignTypes.SNS_ZONE)?.value == null) {
            scanningResultData.stressResponse = ""
        } else {
            val value = (finalResults.getResult(VitalSignTypes.SNS_ZONE) as VitalSignSNSZone).value
            scanningResultData.stressResponse = if (value.name.length > 1) {
                value.name.substring(0, 1) + value.name.substring(1, value.name.length).lowercase()
            } else {
                Utils.getStressResponseAndRecoveryAbility(value.ordinal)
            }
        }

        if (finalResults.getResult(VitalSignTypes.PNS_ZONE)?.value == null) {
            scanningResultData.recoveryAbility = ""
        } else {
            val value = (finalResults.getResult(VitalSignTypes.PNS_ZONE) as VitalSignPNSZone).value
            scanningResultData.recoveryAbility = if (value.name.length > 1) {
                value.name.substring(0, 1) + value.name.substring(1, value.name.length).lowercase()
            } else {
                Utils.getStressResponseAndRecoveryAbility(value.ordinal)
            }
        }

        if (handlerPublishReport != null) {
            handlerPublishReport!!.removeCallbacksAndMessages(null)
        }
        handlerPublishReport = Handler(Looper.getMainLooper())
        handlerPublishReport!!.postDelayed({
            isResultPublishedTimePassed = true
            if (handlerPublishReport != null) {

                var request: SendScanRequest? = null

                if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
                    request = SendScanRequest(
                        scanningResultData.bloodPressure,
                        scanningResultData.hba1c,
                        scanningResultData.heartRate,
                        scanningResultData.hemoglobin,
                        scanningResultData.hrvSdnn,
                        scanningResultData.oxygenSaturation,
                        Enums.SessionMode.FACE.toString(),
                        scanningResultData.stressLevel,
                        scanningResultData.stressResponse,
                        scanningResultData.breathingRate,
                        scanningResultData.prq,
                        scanningResultData.wellnessScore,
                        scanningResultData.recoveryAbility,
                        scanningResultData.heartRateConfLevel,
                        scanningResultData.breathingRateConfLevel,
                        scanningResultData.prqConfLevel,
                        scanningResultData.hrvSdnnConfLevel,
                        "",
                        0,
                        Enums.ScanStatus.COMPLETE.toString()
                    )
                } else {
//                    request = SendScanRequest(
//                        scanningResultData.bloodPressure, //e
//                        "-1",
//                        scanningResultData.heartRate, //e
//                        "-1",
//                        "-1",
//                        scanningResultData.oxygenSaturation, //e
//                        Enums.SessionMode.FACE.toString(),
//                        -1,
//                        "-1",
//                        scanningResultData.breathingRate, //e
//                        scanningResultData.prq, //e
//                        scanningResultData.wellnessScore,
//                        "-1",
//                        scanningResultData.heartRateConfLevel,
//                        scanningResultData.breathingRateConfLevel,
//                        scanningResultData.prqConfLevel,
//                        scanningResultData.hrvSdnnConfLevel,
//                        "",
//                        0
//                    )

                    request = SendScanRequest(
                        "-1", //e
                        "-1", scanningResultData.heartRate, //e
                        "-1", "-1", scanningResultData.oxygenSaturation, //e
                        Enums.SessionMode.FACE.toString(), -1, "-1", "-1", //e
                        "-1", //e
                        scanningResultData.wellnessScore, "-1", scanningResultData.heartRateConfLevel, "-1", "-1", "-1", "", 1, Enums.ScanStatus.COMPLETE.toString()
                    )
                }

//                if (request.bloodPressure == "0" && request.heartRate == "0" && request.oxygenSaturation == "0" && request.prq == "0") {
                if (request.heartRate == "0" && request.oxygenSaturation == "0") {
                    Log.d(TAG, "No result calculated")
                    isFromStop = false
                    val request = ScanStatusRequest(scanStatus = Enums.ScanStatus.FAILED.toString(), 0, getString(R.string.user_stopped_scan))
                    onBoardingViewModel.scanStatus(request, Pref.generateCodeObject?.id!!)

                } else {
                    onBoardingViewModel.sendScanResult(Pref.generateCodeObject?.generateCode, request)
                }
            }
        }, Constant.RESULT_SCREEN_DELAY_TIME.toLong())
    }


    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityScanBinding) {
                (binding as ActivityScanBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            } else if (binding is ActivityScanRBinding) {
                (binding as ActivityScanRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            }
        } else {
            if (binding is ActivityScanBinding) {
                (binding as ActivityScanBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            } else if (binding is ActivityScanRBinding) {
                (binding as ActivityScanRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    try {
                        if (mediaPlayer?.isPlaying == true) {
                            mediaPlayer?.stop()
                            mediaPlayer = null
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mediaPlayer = MediaPlayer()
                    if (session?.state == SessionState.INITIALIZING || session?.state == SessionState.READY) {
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_instruction)
                    } else if (session?.state == SessionState.STARTING || session?.state == SessionState.PROCESSING) {
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_scan_start)
                    } else if (isErrorOrWarning) {
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_scanning_error)
                    } else {
                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_instruction)
                    }
                    mediaPlayer?.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 500)
        }
    }

    private fun stopPrompt() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}