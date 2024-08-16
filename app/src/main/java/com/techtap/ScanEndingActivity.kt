package com.techtap

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityScanEndingBinding
import com.techtap.databinding.ActivityScanEndingRBinding
import com.techtap.network.ResponseStatus
import com.techtap.network.Status
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ScanEndingActivity : BaseActivity(), View.OnClickListener {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding: ViewDataBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var scanId: String? = null
    private var measurementDetail: MeasurementResponse? = null
    private var wellnessLevelStr: String? = null

    companion object {
        fun startActivity(activity: Activity, id: Long) {
            Intent(activity, ScanEndingActivity::class.java).apply {
                putExtra(Constant.IntentConstant.ID, id)
            }.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_ending)
        init()
        initObserver()
//        setValue()

    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.getScanInfoState.collect {
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
//                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null) {
                            measurementDetail = it.data
                        }
                        setValue()
                    }

                    Status.ERROR -> {
//                        showHideProgress(false)
//                        Utils.showErrorMessage(this@ScanEndingActivity, it.message)
                        setValue()
                    }
                }
            }
        }
    }

    private fun getScanInfo() {
        if (Utils.isNetworkAvailable(this)) {
            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 0
            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 0
            }
            onBoardingViewModel.getScanInfo(scanId)
        } else {
            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 3
            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 3
            }
        }
    }

    private fun init() {
        scanId = Pref.generateCodeObject?.generateCode
//        scanId = "Z62"
    }

    private fun setValue() {

        if (measurementDetail?.result != null) {

//            if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
//                if (binding is ActivityScanEndingBinding) {
//                    (binding as ActivityScanEndingBinding).btnSeeReport.visibility = View.VISIBLE
//                } else if (binding is ActivityScanEndingRBinding) {
//                    (binding as ActivityScanEndingRBinding).btnSeeReport.visibility = View.VISIBLE
//                }
//            } else {
//                if (binding is ActivityScanEndingBinding) {
//                    (binding as ActivityScanEndingBinding).btnSeeReport.visibility = View.GONE
//                } else if (binding is ActivityScanEndingRBinding) {
//                    (binding as ActivityScanEndingRBinding).btnSeeReport.visibility = View.GONE
//                }
//            }


            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 1
            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 1
            }

            val wellnessScore = measurementDetail?.basicInfo?.wellnessScore!!
            wellnessLevelStr = Utils.getWellnessLevel(this, wellnessScore)
            playPrompt()

            val finalString = String.format(getString(R.string.use_scan_id_as_x), scanId)
            val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
            val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

            val useScanIdSpan = Spannable.Factory.getInstance().newSpannable(finalString)
            useScanIdSpan.setSpan(boldSpan, 4, (4 + (scanId?.length!!)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (binding is ActivityScanEndingBinding) {

                (binding as ActivityScanEndingBinding).tvWellnessScore.text = wellnessLevelStr
                (binding as ActivityScanEndingBinding).tvScanId.text = String.format(getString(R.string.scan_id_x), scanId)
                (binding as ActivityScanEndingBinding).ivFlower.setImageResource(Utils.getWellnessImage(this, wellnessScore))

                (binding as ActivityScanEndingBinding).tvUseScanIdAs.text = useScanIdSpan
                (binding as ActivityScanEndingBinding).tvUseScanIdAs.movementMethod = LinkMovementMethod.getInstance()
                if (!Pref.generateCodeObject?.url.isNullOrBlank()) {
                    Glide.with(this).load(Pref.generateCodeObject?.url).into((binding as ActivityScanEndingBinding).ivOnMobileQr)
                }
            } else if (binding is ActivityScanEndingRBinding) {

                (binding as ActivityScanEndingRBinding).tvWellnessScore.text = wellnessLevelStr
                (binding as ActivityScanEndingRBinding).tvScanId.text = String.format(getString(R.string.scan_id_x), scanId)
                (binding as ActivityScanEndingRBinding).ivFlower.setImageResource(Utils.getWellnessImage(this, wellnessScore))

                (binding as ActivityScanEndingRBinding).tvUseScanIdAs.text = useScanIdSpan
                (binding as ActivityScanEndingRBinding).tvUseScanIdAs.movementMethod = LinkMovementMethod.getInstance()
                if (!Pref.generateCodeObject?.url.isNullOrBlank()) {
                    Glide.with(this).load(Pref.generateCodeObject?.url).into((binding as ActivityScanEndingRBinding).ivOnMobileQr)
                }
            }

        } else {
            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).viewFlipper.displayedChild = 2
                (binding as ActivityScanEndingBinding).llNoResult.tvNoResult.text = getString(R.string.scan_result_not_found)

            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).viewFlipper.displayedChild = 2
                (binding as ActivityScanEndingRBinding).llNoResult.tvNoResult.text = getString(R.string.scan_result_not_found)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
//        setValue()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_scan_ending_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_scan_ending)
        }
        initClick()
        initSpan()
        getScanInfo()
        setPromptIcon()

    }

    private fun initClick() {
        if (binding is ActivityScanEndingBinding) {
            (binding as ActivityScanEndingBinding).onClickListener = this
        } else if (binding is ActivityScanEndingRBinding) {
            (binding as ActivityScanEndingRBinding).onClickListener = this
        }

        if (binding is ActivityScanEndingBinding) {
            initNoInternet((binding as ActivityScanEndingBinding).llNoInternet) {
                getScanInfo()
            }
        } else if (binding is ActivityScanEndingRBinding) {
            initNoInternet((binding as ActivityScanEndingRBinding).llNoInternet) {
                getScanInfo()
            }
        }
    }

    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

        val iHaveReadSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.i_have_read))

        iHaveReadSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(v: View) {

                }

                override fun updateDrawState(textPaint: TextPaint) {
                    textPaint.isUnderlineText = false
                }
            }, 25, 80, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        iHaveReadSpan.setSpan(boldSpan, 25, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val myVitalSignBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        val myVitalSignSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.my_vital_scan_is_designed))
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan1, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan2, 77, 89, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan3: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan3, 92, 95, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val weCareAboutPrivacySpanBoldTypefaceSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        val weCareAboutPrivacySpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.we_care_about_privacy))
        weCareAboutPrivacySpan.setSpan(weCareAboutPrivacySpanBoldTypefaceSpan, 0, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val deleteDataBoldTypefaceSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        val deleteDataSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.delete_your_data))
        deleteDataSpan.setSpan(deleteDataBoldTypefaceSpan, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val boldTextSpan = StyleSpan(Typeface.BOLD)
        deleteDataSpan.setSpan(boldTextSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        deleteDataSpan.setSpan(RelativeSizeSpan(.8f), 7, getString(R.string.delete_your_data).length, 0); // set size


        if (binding is ActivityScanEndingBinding) {
            (binding as ActivityScanEndingBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityScanEndingBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()

//            (binding as ActivityScanEndingBinding).tvWeCareAboutPrivacy.text = weCareAboutPrivacySpan
//            (binding as ActivityScanEndingBinding).tvWeCareAboutPrivacy.movementMethod = LinkMovementMethod.getInstance()

        } else if (binding is ActivityScanEndingRBinding) {
//            (binding as ActivityScanEndingRBinding).tvWeCareAboutPrivacy.text = weCareAboutPrivacySpan
//            (binding as ActivityScanEndingRBinding).tvWeCareAboutPrivacy.movementMethod = LinkMovementMethod.getInstance()


            (binding as ActivityScanEndingRBinding).tvDelete.text = deleteDataSpan
            (binding as ActivityScanEndingRBinding).tvDelete.movementMethod = LinkMovementMethod.getInstance()
        }

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

//                if (mediaPlayer?.isPlaying != true) {
//                    mediaPlayer = MediaPlayer()
//                    if (wellnessLevelStr != null) {
//                        if (wellnessLevelStr.equals(getString(R.string.great))) {
//                            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_great)
//                        } else if (wellnessLevelStr.equals(getString(R.string.excellent))) {
//                            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_excellent)
//                        } else if (wellnessLevelStr.equals(getString(R.string.can_be_improved))) {
//                            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_can_be_improved)
//                        }
//                    }
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_back -> {
                Pref.generateCodeObject = null
                SplashActivity.startActivityClearTop(this)
            }

            R.id.btn_see_report -> {
                if (measurementDetail != null && measurementDetail?.basicInfo != null) {
                    FullReportActivity.startActivity(this@ScanEndingActivity, measurementDetail)
                }
            }

            R.id.btn_delete_report -> {
                DeleteDataActivity.startActivity(this, scanId)
            }

            R.id.iv_exit -> {
//                ExitActivity.startActivity(this)
                Pref.generateCodeObject = null
                SplashActivity.startActivityClearTop(this)
            }

            R.id.iv_low_view -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            }
        } else {
            if (binding is ActivityScanEndingBinding) {
                (binding as ActivityScanEndingBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            } else if (binding is ActivityScanEndingRBinding) {
                (binding as ActivityScanEndingRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    if (mediaPlayer?.isPlaying != true) {
                        mediaPlayer = MediaPlayer()
                        if (wellnessLevelStr != null) {
                            if (wellnessLevelStr.equals(getString(R.string.great))) {
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_great)
                            } else if (wellnessLevelStr.equals(getString(R.string.excellent))) {
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_excellent)
                            } else if (wellnessLevelStr.equals(getString(R.string.can_be_improved))) {
                                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_wellness_score_can_be_improved)
                            }
                        }
                        mediaPlayer?.start()
                    }
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

