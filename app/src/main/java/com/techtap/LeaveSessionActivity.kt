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
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityDeleteDataBinding
import com.techtap.databinding.ActivityDeleteDataRBinding
import com.techtap.databinding.ActivityLeaveSessionBinding
import com.techtap.databinding.ActivityLeaveSessionRBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityScanBinding
import com.techtap.databinding.ActivityScanRBinding
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Pref
import com.techtap.viewmodel.OnBoardingViewModel

class LeaveSessionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ViewDataBinding
    private var mediaPlayer: MediaPlayer? = null
    private var scanId: String? = null

    companion object {
        fun startActivity(activity: Activity, scanId: String?) {
            Intent(activity, LeaveSessionActivity::class.java).apply {
                putExtra(Constant.IntentConstant.ID, scanId)
            }.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        playPrompt()
    }

    private fun init() {
        scanId = intent.getStringExtra(Constant.IntentConstant.ID)
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_leave_session_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_leave_session)
        }
        initClick()
        setValues()
        setPromptIcon()
//        initSpan()
    }

    private fun setValues() {
//        Glide.with(this).load(R.raw.ic_scanning_bg).into(binding.ivCameraView)

        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

        val toProtectYourPrivacy = String.format(getString(R.string.to_protect_your_privacy), scanId)
        val toProtectYourPrivacySpan = Spannable.Factory.getInstance().newSpannable(toProtectYourPrivacy)
        toProtectYourPrivacySpan.setSpan(boldSpan, 91, (91 + (scanId?.length!!)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        if (binding is ActivityLeaveSessionBinding) {
            (binding as ActivityLeaveSessionBinding).lblToProtectYourPrivacy.text = toProtectYourPrivacySpan
        } else if (binding is ActivityLeaveSessionRBinding) {
            (binding as ActivityLeaveSessionRBinding).lblToProtectYourPrivacy.text = toProtectYourPrivacySpan
        }
    }

    private fun initClick() {
        if (binding is ActivityLeaveSessionBinding) {
            (binding as ActivityLeaveSessionBinding).onClickListener = this
        } else if (binding is ActivityLeaveSessionRBinding) {
            (binding as ActivityLeaveSessionRBinding).onClickListener = this
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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_leave_session)
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_low_view -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }

            R.id.btn_yes_leave -> {
                SplashActivity.startActivityClearTop(this)
                finish()
            }

            R.id.btn_stay -> {
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }


    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityLeaveSessionBinding) {
                (binding as ActivityLeaveSessionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            } else if (binding is ActivityLeaveSessionRBinding) {
                (binding as ActivityLeaveSessionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            }
        } else {
            if (binding is ActivityLeaveSessionBinding) {
                (binding as ActivityLeaveSessionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            } else if (binding is ActivityLeaveSessionRBinding) {
                (binding as ActivityLeaveSessionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_leave_session)
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