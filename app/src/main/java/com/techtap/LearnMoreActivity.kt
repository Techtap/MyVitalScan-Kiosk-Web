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
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityLearnMoreBinding
import com.techtap.databinding.ActivityLearnMoreRBinding
import com.techtap.databinding.ActivityPersonaSelectionBinding
import com.techtap.databinding.ActivityPersonaSelectionRBinding
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Pref

class LearnMoreActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ViewDataBinding
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, LearnMoreActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(
//                    activity,
//                    AnimationsHandler.Animations.FadeEnter
//                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//         binding = DataBindingUtil.setContentView(this, R.layout.activity_learn_more)

        playPrompt()
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_learn_more_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_learn_more)
        }
        initClick()
        initSpan()
        initTabs()
    }

    private fun initClick() {
        if (binding is ActivityLearnMoreBinding) {
            (binding as ActivityLearnMoreBinding).onClickListener = this
        } else if (binding is ActivityLearnMoreRBinding) {
            (binding as ActivityLearnMoreRBinding).onClickListener = this
        }
    }


    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")

        val myVitalSignSpan = Spannable.Factory.getInstance()
            .newSpannable(getString(R.string.my_vital_scan_is_designed))

        val myVitalSignBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(
            myVitalSignBoldTypefaceSpan1,
            0,
            11,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val myVitalSignBoldTypefaceSpan2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(
            myVitalSignBoldTypefaceSpan2,
            77,
            89,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val myVitalSignBoldTypefaceSpan3: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(
            myVitalSignBoldTypefaceSpan3,
            92,
            95,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        val theShapeOfOurDigital = Spannable.Factory.getInstance()
            .newSpannable(getString(R.string.the_shape_of_our_digital))

        val theShapeOfOurDigital1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        theShapeOfOurDigital.setSpan(
            theShapeOfOurDigital1,
            17,
            47,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        val vitalScanOffers = Spannable.Factory.getInstance()
            .newSpannable(getString(R.string.myVitalScan_offers_easy_integration))

        val vitalScanOffers1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        vitalScanOffers.setSpan(vitalScanOffers1, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val vitalScanOffers2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        vitalScanOffers.setSpan(vitalScanOffers2, 100, 104, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val vitalScanOffers3: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        vitalScanOffers.setSpan(vitalScanOffers3, 109, 113, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//        healthcare para span
        initSpanHealthCare()

//        wellness para span
        initSpanWellness()

//        insurance para span
        initSpanInsurance()

        val scanTheQrCode = Spannable.Factory.getInstance()
            .newSpannable(getString(R.string.scan_the_qr_code))

        val scanTheQrCode1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        scanTheQrCode.setSpan(scanTheQrCode1, 68, 79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (binding is ActivityLearnMoreBinding) {

            (binding as ActivityLearnMoreBinding).tvTheShapeOfOurDigital.text = theShapeOfOurDigital
            (binding as ActivityLearnMoreBinding).myVitalScanOffers.text = vitalScanOffers
            (binding as ActivityLearnMoreBinding).tvScanTheQrCode.text = scanTheQrCode


            (binding as ActivityLearnMoreBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityLearnMoreBinding).tvMyVitalScan.movementMethod =
                LinkMovementMethod.getInstance()

        } else if (binding is ActivityLearnMoreRBinding) {
            (binding as ActivityLearnMoreRBinding).tvTheShapeOfOurDigital.text = theShapeOfOurDigital
            (binding as ActivityLearnMoreRBinding).myVitalScanOffers.text = vitalScanOffers
            (binding as ActivityLearnMoreRBinding).tvScanTheQrCode.text = scanTheQrCode


            (binding as ActivityLearnMoreRBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityLearnMoreRBinding).tvMyVitalScan.movementMethod =
                LinkMovementMethod.getInstance()
        }


//        if (binding is ActivityPersonaSelectionBinding) {
//            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.text = myVitalSignSpan
//            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
//        } else if (binding is ActivityPersonaSelectionRBinding) {
//            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.text = myVitalSignSpan
//            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
//        }
    }

    private fun initSpanHealthCare() {

        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")

        val tabHealthcarePara1 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_healthcare_para1))

        val tabHealthcarePara11: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara1.setSpan(tabHealthcarePara11, 43, 48, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tabHealthcarePara12: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara1.setSpan(tabHealthcarePara12, 168, 195, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tabHealthcarePara13: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara1.setSpan(tabHealthcarePara13, 201, 230, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tabHealthcarePara14: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara1.setSpan(tabHealthcarePara14, 236, 258, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val tabHealthcarePara2 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_healthcare_para2))

        val tabHealthcarePara21: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara2.setSpan(tabHealthcarePara21, 39, 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tabHealthcarePara22: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara2.setSpan(tabHealthcarePara22, 118, 142, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val tabHealthcarePara23: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara2.setSpan(tabHealthcarePara23, 147, 196, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabHealthcarePara3 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_healthcare_para3))

        val tabHealthcarePara31: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabHealthcarePara3.setSpan(tabHealthcarePara31, 31, 36, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        if (binding is ActivityLearnMoreBinding) {

            (binding as ActivityLearnMoreBinding).tvHealthcarePara1.text = tabHealthcarePara1
            (binding as ActivityLearnMoreBinding).tvHealthcarePara2.text = tabHealthcarePara2
            (binding as ActivityLearnMoreBinding).tvHealthcarePara3.text = tabHealthcarePara3

        } else if (binding is ActivityLearnMoreRBinding) {
            (binding as ActivityLearnMoreRBinding).tvHealthcarePara1.text = tabHealthcarePara1
            (binding as ActivityLearnMoreRBinding).tvHealthcarePara2.text = tabHealthcarePara2
            (binding as ActivityLearnMoreRBinding).tvHealthcarePara3.text = tabHealthcarePara3
        }
    }

    private fun initSpanWellness() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val tabWellnessPara1 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_wellness_para1))

        val tabWellness11: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara1.setSpan(tabWellness11, 44, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellnessPara2 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_wellness_para2))

        val tabWellness21: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara2.setSpan(tabWellness21, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness22: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara2.setSpan(tabWellness22, 33, 77, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness23: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara2.setSpan(tabWellness23, 83, 108, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabWellnessPara3 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_wellness_para3))

        val tabWellness33: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara3.setSpan(tabWellness33, 192, 230, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness34: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara3.setSpan(tabWellness34, 236, 256, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//
        val tabWellnessPara4 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_wellness_para4))

        val tabWellness41: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara4.setSpan(tabWellness41, 179, 190, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness42: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara4.setSpan(tabWellness42, 240, 289, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness43: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara4.setSpan(tabWellness43, 294, 328, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//
        val tabWellnessPara5 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_wellness_para5))

        val tabWellness51: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara5.setSpan(tabWellness51, 236, 259, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness52: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara5.setSpan(tabWellness52, 268, 279, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness53: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara5.setSpan(tabWellness53, 287, 348, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabWellness54: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabWellnessPara5.setSpan(tabWellness54, 422, 460, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//


        if (binding is ActivityLearnMoreBinding) {

            (binding as ActivityLearnMoreBinding).tvWellnessPara1.text = tabWellnessPara1
            (binding as ActivityLearnMoreBinding).tvWellnessPara2.text = tabWellnessPara2
            (binding as ActivityLearnMoreBinding).tvWellnessPara3.text = tabWellnessPara3
            (binding as ActivityLearnMoreBinding).tvWellnessPara4.text = tabWellnessPara4
            (binding as ActivityLearnMoreBinding).tvWellnessPara5.text = tabWellnessPara5

        } else if (binding is ActivityLearnMoreRBinding) {
//            (binding as ActivityLearnMoreRBinding).tvHealthcarePara1.text = tabHealthcarePara1
//            (binding as ActivityLearnMoreRBinding).tvHealthcarePara2.text = tabHealthcarePara2
//            (binding as ActivityLearnMoreRBinding).tvWellnessPara.text = tabWellnessPara

            (binding as ActivityLearnMoreRBinding).tvWellnessPara1.text = tabWellnessPara1
            (binding as ActivityLearnMoreRBinding).tvWellnessPara2.text = tabWellnessPara2
            (binding as ActivityLearnMoreRBinding).tvWellnessPara3.text = tabWellnessPara3
            (binding as ActivityLearnMoreRBinding).tvWellnessPara4.text = tabWellnessPara4
            (binding as ActivityLearnMoreRBinding).tvWellnessPara5.text = tabWellnessPara5

        }
    }

    private fun initSpanInsurance() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")

        val tabInsurancePara1 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para1))

        val tabInsurancePara11: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara1.setSpan(tabInsurancePara11, 223, 297, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //

        val tabInsurancePara2 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para2))

        val tabInsurancePara21: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara2.setSpan(tabInsurancePara21, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabInsurancePara22: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara2.setSpan(tabInsurancePara22, 148, 208, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara3 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para3))

        val tabInsurancePara31: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara3.setSpan(tabInsurancePara31, 0, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val tabInsurancePara32: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara3.setSpan(tabInsurancePara32, 144, 171, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara4 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para4))

        val tabInsurancePara41: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara4.setSpan(tabInsurancePara41, 6, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara5 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para5))

        val tabInsurancePara51: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara5.setSpan(tabInsurancePara51, 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara6 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para6))

        val tabInsurancePara61: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara6.setSpan(tabInsurancePara61, 0, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara7 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para7))

        val tabInsurancePara71: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara7.setSpan(tabInsurancePara71, 0, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
        val tabInsurancePara8 =
            Spannable.Factory.getInstance().newSpannable(getString(R.string.tab_insurance_para8))

        val tabInsurancePara81: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        tabInsurancePara8.setSpan(tabInsurancePara81, 0, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (binding is ActivityLearnMoreBinding) {

            (binding as ActivityLearnMoreBinding).tvInsurancePara1.text = tabInsurancePara1
            (binding as ActivityLearnMoreBinding).tvInsurancePara2.text = tabInsurancePara2
            (binding as ActivityLearnMoreBinding).tvInsurancePara3.text = tabInsurancePara3
            (binding as ActivityLearnMoreBinding).tvInsurancePara4.text = tabInsurancePara4
            (binding as ActivityLearnMoreBinding).tvInsurancePara5.text = tabInsurancePara5
            (binding as ActivityLearnMoreBinding).tvInsurancePara6.text = tabInsurancePara6
            (binding as ActivityLearnMoreBinding).tvInsurancePara7.text = tabInsurancePara7
            (binding as ActivityLearnMoreBinding).tvInsurancePara8.text = tabInsurancePara8


        } else if (binding is ActivityLearnMoreRBinding) {
//            (binding as ActivityLearnMoreRBinding).tvHealthcarePara1.text = tabHealthcarePara1
//            (binding as ActivityLearnMoreRBinding).tvHealthcarePara2.text = tabHealthcarePara2
//            (binding as ActivityLearnMoreRBinding).tvWellnessPara.text = tabWellnessPara

            (binding as ActivityLearnMoreRBinding).tvInsurancePara1.text = tabInsurancePara1
            (binding as ActivityLearnMoreRBinding).tvInsurancePara2.text = tabInsurancePara2
            (binding as ActivityLearnMoreRBinding).tvInsurancePara3.text = tabInsurancePara3
            (binding as ActivityLearnMoreRBinding).tvInsurancePara4.text = tabInsurancePara4
            (binding as ActivityLearnMoreRBinding).tvInsurancePara5.text = tabInsurancePara5
            (binding as ActivityLearnMoreRBinding).tvInsurancePara6.text = tabInsurancePara6
            (binding as ActivityLearnMoreRBinding).tvInsurancePara7.text = tabInsurancePara7
            (binding as ActivityLearnMoreRBinding).tvInsurancePara8.text = tabInsurancePara8
        }
    }

    private fun initTabs() {
        if (binding is ActivityLearnMoreBinding) {
            (binding as ActivityLearnMoreBinding).radioGroup.setOnCheckedChangeListener { parent, _ ->
                when (parent.checkedRadioButtonId) {
                    R.id.healthcare_radio -> {
                        (binding as ActivityLearnMoreBinding).clHealthcarePara.visibility =
                            View.VISIBLE
                        (binding as ActivityLearnMoreBinding).clWellnessPara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreBinding).clInsurancePara.visibility =
                            View.INVISIBLE

                        (binding as ActivityLearnMoreBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_healthcare)
                    }

                    R.id.wellness_radio -> {
                        (binding as ActivityLearnMoreBinding).clHealthcarePara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreBinding).clWellnessPara.visibility =
                            View.VISIBLE
                        (binding as ActivityLearnMoreBinding).clInsurancePara.visibility =
                            View.INVISIBLE

                        (binding as ActivityLearnMoreBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_wellness)
                    }

                    R.id.insurance_radio -> {
                        (binding as ActivityLearnMoreBinding).clHealthcarePara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreBinding).clWellnessPara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreBinding).clInsurancePara.visibility =
                            View.VISIBLE

                        (binding as ActivityLearnMoreBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_insurance)
                    }
                }
            }
        } else if (binding is ActivityLearnMoreRBinding) {
            (binding as ActivityLearnMoreRBinding).radioGroup.setOnCheckedChangeListener { parent, _ ->
                when (parent.checkedRadioButtonId) {
                    R.id.healthcare_radio -> {
                        (binding as ActivityLearnMoreRBinding).clHealthcarePara.visibility =
                            View.VISIBLE
                        (binding as ActivityLearnMoreRBinding).clWellnessPara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreRBinding).clInsurancePara.visibility =
                            View.INVISIBLE

                        (binding as ActivityLearnMoreRBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_healthcare)
                    }

                    R.id.wellness_radio -> {
                        (binding as ActivityLearnMoreRBinding).clHealthcarePara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreRBinding).clWellnessPara.visibility =
                            View.VISIBLE
                        (binding as ActivityLearnMoreRBinding).clInsurancePara.visibility =
                            View.INVISIBLE

                        (binding as ActivityLearnMoreRBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_wellness)
                    }

                    R.id.insurance_radio -> {
                        (binding as ActivityLearnMoreRBinding).clHealthcarePara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreRBinding).clWellnessPara.visibility =
                            View.INVISIBLE
                        (binding as ActivityLearnMoreRBinding).clInsurancePara.visibility =
                            View.VISIBLE

                        (binding as ActivityLearnMoreRBinding).ivQrMvs.background =
                            AppCompatResources.getDrawable(this, R.drawable.ic_qr_mvs_insurance)
                    }
                }
            }
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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_learn_more)
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_low_view -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }

            R.id.iv_exit -> {
//                ExitActivity.startActivity(this)
//                finish()
                SplashActivity.startActivityClearTop(this)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer = null
        }
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityLearnMoreBinding) {
                (binding as ActivityLearnMoreBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            } else if (binding is ActivityLearnMoreRBinding) {
                (binding as ActivityLearnMoreRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            }
        } else {
            if (binding is ActivityLearnMoreBinding) {
                (binding as ActivityLearnMoreBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            } else if (binding is ActivityLearnMoreRBinding) {
                (binding as ActivityLearnMoreRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_learn_more)
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