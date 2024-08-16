package com.techtap

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityScanBinding
import com.techtap.databinding.ActivityScanRBinding
import com.techtap.network.Status
import com.techtap.utils.AnimationsHandler
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Logger
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PrivacyPolicyTermsConditionActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ViewDataBinding
    private var isPrivacySelected: Boolean = false
    private var mediaPlayer: MediaPlayer? = null
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var clickCount = 0

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, PrivacyPolicyTermsConditionActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy_terms_condition)
//        initClick()
//        initSpan()
        initObserver()

        playPrompt()


//        val dm: DisplayMetrics = resources.displayMetrics
//        val densityDpi = dm.densityDpi
//        val height: Int = dm.heightPixels
//        val width: Int = dm.widthPixels
//        Toast.makeText(this, "DPI = $densityDpi  \n H = $height \t W=$width", Toast.LENGTH_SHORT).show()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.generateCodeState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null) {
                            Logger.e("Generate Code = ${it.data.generateCode}")
                            Logger.e("Url = ${it.data.url}")
                            Logger.e("Id = ${it.data.id}")
                            Pref.generateCodeObject = it.data
//                            PersonaSelectionActivity.startActivity(this@PrivacyPolicyTermsConditionActivity)

                            if (BuildConfig.FLAVOR.equals(Constant.EXTERNAL, true)) {
                                PersonaSelectionActivity.startActivity(this@PrivacyPolicyTermsConditionActivity)
                            } else {
                                QuestionActivity.startActivity(this@PrivacyPolicyTermsConditionActivity)
                            }
                        } else {
                            Utils.showErrorMessage(this@PrivacyPolicyTermsConditionActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        Utils.showErrorMessage(this@PrivacyPolicyTermsConditionActivity, it.message)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setCustomContentView()
    }

    private fun setCustomContentView() {
        binding = if (Pref.isLowView) {
            DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy_terms_condition_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_privacy_policy_terms_condition)
        }
        initClick()
        initSpan()
        setIHaveReadIcon()
        setPromptIcon()


    }

    private fun initClick() {
        if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
            (binding as ActivityPrivacyPolicyTermsConditionBinding).onClickListener = this
        } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).onClickListener = this
        }
    }

    private fun initSpan() {

        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val boldSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)

        val iHaveReadSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.i_have_read))

        iHaveReadSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(v: View) {
                    PrivacyPolicyActivity.startActivity(this@PrivacyPolicyTermsConditionActivity)
                }

                    override fun updateDrawState(textPaint: TextPaint) {
//                    textPaint.color = ContextCompat.getColor(this@PrivacyPolicyTermsConditionActivity, R.color.scan_error_bg)
                    textPaint.isUnderlineText = true
                }
            }, 39, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        iHaveReadSpan.setSpan(
            object : ClickableSpan() {
                override fun onClick(v: View) {
                    TermsConditionActivity.startActivity(this@PrivacyPolicyTermsConditionActivity)
                }

                override fun updateDrawState(textPaint: TextPaint) {
//                    textPaint.color = ContextCompat.getColor(this@PrivacyPolicyTermsConditionActivity, R.color.scan_error_bg)
                    textPaint.isUnderlineText = true
                }
            }, 58, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        iHaveReadSpan.setSpan(boldSpan, 25, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val myVitalSignBoldTypefaceSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        val myVitalSignSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.my_vital_scan_is_designed))
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan1, 0, 11, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan2, 77, 89, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val myVitalSignBoldTypefaceSpan3: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalSignSpan.setSpan(myVitalSignBoldTypefaceSpan3, 92, 95, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        if (binding is ActivityPrivacyPolicyTermsConditionBinding) {

            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvIHaveRead.text = iHaveReadSpan
            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvIHaveRead.movementMethod = LinkMovementMethod.getInstance()
            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvIHaveRead.highlightColor = Color.TRANSPARENT;

            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()

        } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvIHaveRead.text = iHaveReadSpan
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvIHaveRead.movementMethod = LinkMovementMethod.getInstance()
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvIHaveRead.highlightColor = Color.TRANSPARENT;

            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvMyVitalScan.text = myVitalSignSpan
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvMyVitalScan.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_telus_health -> {
                clickCount++
                if (clickCount == 5) {
                    clickCount = 0
                    Pref.isInternal = !Pref.isInternal
                }
            }

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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_terms_and_condition)
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_exit -> {
//                ExitActivity.startActivity(this)
                SplashActivity.startActivityClearTop(this)
            }

            R.id.iv_i_have_read -> {
                isPrivacySelected = !isPrivacySelected
                setIHaveReadIcon()
//                if (isPrivacySelected) {
//                    if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
//                        (binding as ActivityPrivacyPolicyTermsConditionBinding).ivIHaveRead.setImageResource(R.drawable.rb_selected)
//                        (binding as ActivityPrivacyPolicyTermsConditionBinding).btnContinue.alpha = 1f
//                    } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
//                        (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivIHaveRead.setImageResource(R.drawable.rb_selected)
//                        (binding as ActivityPrivacyPolicyTermsConditionRBinding).btnContinue.alpha = 1f
//                    }
//                } else {
//                    if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
//                        (binding as ActivityPrivacyPolicyTermsConditionBinding).ivIHaveRead.setImageResource(R.drawable.rb_default)
//                        (binding as ActivityPrivacyPolicyTermsConditionBinding).btnContinue.alpha = 0.5f
//                    } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
//                        (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivIHaveRead.setImageResource(R.drawable.rb_default)
//                        (binding as ActivityPrivacyPolicyTermsConditionRBinding).btnContinue.alpha = 0.5f
//                    }
//                }
            }

            R.id.btn_continue -> {
                if (!isPrivacySelected) {
//                    Utils.showErrorMessage(this, getString(R.string.please_accept_to_proceed))
                } else {
                    onBoardingViewModel.generateCode()
                }
            }

            R.id.iv_low_view -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }
        }
    }

    private fun setIHaveReadIcon() {
        if (isPrivacySelected) {
            if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionBinding).ivIHaveRead.setImageResource(R.drawable.rb_selected)
                (binding as ActivityPrivacyPolicyTermsConditionBinding).btnContinue.alpha = 1f

                (binding as ActivityPrivacyPolicyTermsConditionBinding).tvPleaseAccept.clearAnimation()
                (binding as ActivityPrivacyPolicyTermsConditionBinding).tvPleaseAccept.visibility = View.INVISIBLE


            } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivIHaveRead.setImageResource(R.drawable.rb_selected)
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).btnContinue.alpha = 1f

                (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvPleaseAccept.clearAnimation()
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvPleaseAccept.visibility = View.INVISIBLE
            }
        } else {
            if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionBinding).ivIHaveRead.setImageResource(R.drawable.rb_default)
                (binding as ActivityPrivacyPolicyTermsConditionBinding).btnContinue.alpha = 0.5f
                startFadeAnimation()
            } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivIHaveRead.setImageResource(R.drawable.rb_default)
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).btnContinue.alpha = 0.5f

                startFadeAnimation()

            }
        }
    }

    private fun startFadeAnimation() {
        val inAnimation: Animation = AlphaAnimation(0.5f, 1.0f)
        inAnimation.repeatCount = Animation.INFINITE
        inAnimation.repeatMode = Animation.REVERSE
        inAnimation.duration = 1000
        if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvPleaseAccept.visibility = View.VISIBLE
            (binding as ActivityPrivacyPolicyTermsConditionBinding).tvPleaseAccept.startAnimation(inAnimation)
        } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvPleaseAccept.startAnimation(inAnimation)
            (binding as ActivityPrivacyPolicyTermsConditionRBinding).tvPleaseAccept.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            }
        } else {
            if (binding is ActivityPrivacyPolicyTermsConditionBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            } else if (binding is ActivityPrivacyPolicyTermsConditionRBinding) {
                (binding as ActivityPrivacyPolicyTermsConditionRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_terms_and_condition)
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