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
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPersonaSelectionBinding
import com.techtap.databinding.ActivityPersonaSelectionRBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityScanBinding
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Pref
import com.techtap.utils.Utils

class PersonaSelectionActivity : BaseActivity(), View.OnClickListener {
    private var mediaPlayer: MediaPlayer? = null
    private var timeOutHandler: Handler? = null

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, PersonaSelectionActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    private lateinit var binding: ViewDataBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_persona_selection)

//        initClick()
//        initSpan()
//        setValues()

        playPrompt()
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()

    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_persona_selection_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_persona_selection)
        }
        initClick()
        initSpan()
        setValues()
        setPromptIcon()
    }


    private fun initClick() {
        if (binding is ActivityPersonaSelectionBinding) {
            (binding as ActivityPersonaSelectionBinding).onClickListener = this
        } else if (binding is ActivityPersonaSelectionRBinding) {
            (binding as ActivityPersonaSelectionRBinding).onClickListener = this
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

        if (binding is ActivityPersonaSelectionBinding) {

            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityPersonaSelectionBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()

        } else if (binding is ActivityPersonaSelectionRBinding) {
            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityPersonaSelectionRBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setValues() {

        if (binding is ActivityPersonaSelectionBinding) {

            Glide.with(this).load(R.raw.fit_and_fine).into((binding as ActivityPersonaSelectionBinding).ivFitAndFine)
            Glide.with(this).load(R.raw.mellow_and_median).into((binding as ActivityPersonaSelectionBinding).ivMellowAndMedian)
            Glide.with(this).load(R.raw.energetic_and_extra).into((binding as ActivityPersonaSelectionBinding).ivEnergeticAndExtra)
            Glide.with(this).load(R.raw.big_and_beautiful).into((binding as ActivityPersonaSelectionBinding).ivBigAndBeautiful)

        } else if (binding is ActivityPersonaSelectionRBinding) {
            Glide.with(this).load(R.raw.fit_and_fine).into((binding as ActivityPersonaSelectionRBinding).ivFitAndFine)
            Glide.with(this).load(R.raw.mellow_and_median).into((binding as ActivityPersonaSelectionRBinding).ivMellowAndMedian)
            Glide.with(this).load(R.raw.energetic_and_extra).into((binding as ActivityPersonaSelectionRBinding).ivEnergeticAndExtra)
            Glide.with(this).load(R.raw.big_and_beautiful).into((binding as ActivityPersonaSelectionRBinding).ivBigAndBeautiful)
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
//                    mediaPlayer =
//                        MediaPlayer.create(applicationContext, R.raw.audio_persona_selection)
//                    mediaPlayer?.start()
//                }
            }

            R.id.rl_fit_and_fine, R.id.iv_fit_and_fine -> {
                ScanActivity.startActivity(this)
            }

            R.id.rl_mellow_and_median, R.id.iv_mellow_and_median -> {
                ScanActivity.startActivity(this)
            }

            R.id.rl_energetic_and_extra, R.id.iv_energetic_and_extra -> {
                ScanActivity.startActivity(this)
            }

            R.id.rl_big_and_beautiful, R.id.iv_big_and_beautiful -> {
                ScanActivity.startActivity(this)
            }

            R.id.iv_back -> {
                finish()
            }

            R.id.iv_exit -> {
//                ExitActivity.startActivity(this)
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
            if (binding is ActivityPersonaSelectionBinding) {
                (binding as ActivityPersonaSelectionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            } else if (binding is ActivityPersonaSelectionRBinding) {
                (binding as ActivityPersonaSelectionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            }
        } else {
            if (binding is ActivityPersonaSelectionBinding) {
                (binding as ActivityPersonaSelectionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            } else if (binding is ActivityPersonaSelectionRBinding) {
                (binding as ActivityPersonaSelectionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_persona_selection)
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