package com.techtap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityScanEndingBinding
import com.techtap.databinding.ActivityScanEndingRBinding
import com.techtap.databinding.ActivityScreenSaverBinding
import com.techtap.databinding.ActivityScreenSaverRBinding
import com.techtap.databinding.ActivityShareWithUsBinding
import com.techtap.databinding.ActivityShareWithUsRBinding
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.DateFormatter
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.utils.Utils.getWellnessLevel
import com.techtap.utils.Utils.showToast

class ShareWithUsActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun startActivity(activity: Activity, measurementDetail: MeasurementResponse?) {
            Intent(activity, ShareWithUsActivity::class.java).apply {
                putExtra(Constant.IntentConstant.SCAN_RESULT, measurementDetail)
            }.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    private var measurementDetail: MeasurementResponse? = null
    private lateinit var binding: ViewDataBinding
    private var scanId: String? = null
    private var wellnessLevelStr: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_share_with_us)
        init()
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
        setValue()
        initSpan()

    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_share_with_us_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_share_with_us)
        }
        initClick()
    }

    private fun initClick() {
        if (binding is ActivityShareWithUsBinding) {
            (binding as ActivityShareWithUsBinding).onClickListener = this
        } else if (binding is ActivityShareWithUsRBinding) {
            (binding as ActivityShareWithUsRBinding).onClickListener = this
        }
    }

    private fun init() {
        measurementDetail = Utils.getParcelable(intent, Constant.IntentConstant.SCAN_RESULT, MeasurementResponse::class.java)
    }

    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")


        val telusMyCareAllowsYouSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.telus_mycare_allows_you_to_book))
        val telusMyCareAllowsYou: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        telusMyCareAllowsYouSpan.setSpan(telusMyCareAllowsYou, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val telusMyCareAllowsYou1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        telusMyCareAllowsYouSpan.setSpan(telusMyCareAllowsYou1, 137, 149, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val telusMyCareAllowsYou2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        telusMyCareAllowsYouSpan.setSpan(telusMyCareAllowsYou2, 225, 231, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val furtherIntegrationSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.further_integration_with_health))
        val furtherIntegrationBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        furtherIntegrationSpan.setSpan(furtherIntegrationBoldTypefaceSpan1, 47, 82, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val theFollowingIsExampleSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.the_following_is_example))
        val theFollowingIsExampleBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        theFollowingIsExampleSpan.setSpan(theFollowingIsExampleBoldTypefaceSpan1, 64, 71, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//

        telusMyCareAllowsYouSpan.setSpan(telusMyCareAllowsYou, 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (binding is ActivityShareWithUsBinding) {
            (binding as ActivityShareWithUsBinding).tvTelusMycareAllowsYou.text = telusMyCareAllowsYouSpan
            (binding as ActivityShareWithUsBinding).tvFurtherIntegrationWithHealth.text = furtherIntegrationSpan
            (binding as ActivityShareWithUsBinding).tvTheFollowingIsExample.text = theFollowingIsExampleSpan

        } else if (binding is ActivityShareWithUsRBinding) {
            (binding as ActivityShareWithUsRBinding).tvTelusMycareAllowsYou.text = telusMyCareAllowsYouSpan
            (binding as ActivityShareWithUsRBinding).tvFurtherIntegrationWithHealth.text = furtherIntegrationSpan
            (binding as ActivityShareWithUsRBinding).tvTheFollowingIsExample.text = theFollowingIsExampleSpan
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setValue() {
        if (measurementDetail?.result != null) {

            scanId = measurementDetail?.basicInfo?.generateCode
            val wellnessScore = measurementDetail?.basicInfo?.wellnessScore!!
            val wellnessLevelStr = Utils.getWellnessLevel(this, wellnessScore)
            val heartRate = measurementDetail?.basicInfo?.heartRate
            val stressLevel = measurementDetail?.basicInfo?.stressLevel
            val breathingRate = measurementDetail?.basicInfo?.breathingRate
            val hrvSdnn = measurementDetail?.basicInfo?.hrvSdnn
            val prq = measurementDetail?.basicInfo?.prq
            val bloodPressure = measurementDetail?.basicInfo?.bloodPressure
            val formattedDate = DateFormatter.getFormattedDate(DateFormatter.SERVER_DATE_FORMAT, measurementDetail?.basicInfo?.generatedAt!!, DateFormatter.DD_dd_MMM_YYYY_hh_mm_ss_UTC)


            val finalString = String.format(getString(R.string.myVitalScan_measured_my_vitals_for), scanId)
            val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
            val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

            val useScanIdSpan = Spannable.Factory.getInstance().newSpannable(finalString)
            useScanIdSpan.setSpan(boldSpan, 37, (37 + (scanId?.length!!)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (binding is ActivityShareWithUsBinding) {
                (binding as ActivityShareWithUsBinding).tvMyVitalScanMeasuredMyVitalsFor.text = useScanIdSpan
                (binding as ActivityShareWithUsBinding).tvWellnessScore.text = String.format(getString(R.string.wellness_score), wellnessScore)

                if (!wellnessLevelStr.isNullOrEmpty()) {
                    (binding as ActivityShareWithUsBinding).tvWellnessLevel.text = String.format(getString(R.string.wellness_level), wellnessLevelStr)
                } else {
                    (binding as ActivityShareWithUsBinding).tvWellnessLevel.text = String.format(getString(R.string.wellness_level), "-")
                }

                (binding as ActivityShareWithUsBinding).tvHeartRate.text = String.format(getString(R.string.heart_rate1), heartRate)
                (binding as ActivityShareWithUsBinding).tvBreathingRate.text = String.format(getString(R.string.breathing_rate), breathingRate)
                if (!stressLevel.isNullOrEmpty()) {
                    (binding as ActivityShareWithUsBinding).tvStressLevel.text = String.format(getString(R.string.stress_level), stressLevel)
                } else {
                    (binding as ActivityShareWithUsBinding).tvStressLevel.text = String.format(getString(R.string.stress_level), "-")
                }

                (binding as ActivityShareWithUsBinding).tvHrvSdnn.text = String.format(getString(R.string.hrv_sdnn), hrvSdnn)
                (binding as ActivityShareWithUsBinding).tvPrq.text = String.format(getString(R.string.prq), prq)
                (binding as ActivityShareWithUsBinding).tvBloodPressure.text = String.format(getString(R.string.blood_pressure), bloodPressure)
                (binding as ActivityShareWithUsBinding).tvTaken.text = String.format(getString(R.string.taken), formattedDate)

            } else if (binding is ActivityShareWithUsRBinding) {
                (binding as ActivityShareWithUsRBinding).tvMyVitalScanMeasuredMyVitalsFor.text = useScanIdSpan
                (binding as ActivityShareWithUsRBinding).tvWellnessScore.text = String.format(getString(R.string.wellness_score), wellnessScore)
                if (!wellnessLevelStr.isNullOrEmpty()) {
                    (binding as ActivityShareWithUsRBinding).tvWellnessLevel.text = String.format(getString(R.string.wellness_level), wellnessLevelStr)
                } else {
                    (binding as ActivityShareWithUsRBinding).tvWellnessLevel.text = String.format(getString(R.string.wellness_level), "-")
                }

                (binding as ActivityShareWithUsRBinding).tvHeartRate.text = String.format(getString(R.string.heart_rate1), heartRate)
                (binding as ActivityShareWithUsRBinding).tvBreathingRate.text = String.format(getString(R.string.breathing_rate), breathingRate)
                if (!stressLevel.isNullOrEmpty()) {
                    (binding as ActivityShareWithUsRBinding).tvStressLevel.text = String.format(getString(R.string.stress_level), stressLevel)
                } else {
                    (binding as ActivityShareWithUsRBinding).tvStressLevel.text = String.format(getString(R.string.stress_level), "-")
                }

                (binding as ActivityShareWithUsRBinding).tvHrvSdnn.text = String.format(getString(R.string.hrv_sdnn), hrvSdnn)
                (binding as ActivityShareWithUsRBinding).tvPrq.text = String.format(getString(R.string.prq), prq)
                (binding as ActivityShareWithUsRBinding).tvBloodPressure.text = String.format(getString(R.string.blood_pressure), bloodPressure)
                (binding as ActivityShareWithUsRBinding).tvTaken.text = String.format(getString(R.string.taken), formattedDate)

            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_close -> {
                finish()
            }
        }
    }
}