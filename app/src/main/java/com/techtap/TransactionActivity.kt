package com.techtap

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityTransactionBinding
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Pref

class TransactionActivity : BaseActivity(), View.OnClickListener {


    private lateinit var binding: ActivityTransactionBinding

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, TransactionActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transaction)
        initClick()
//        initClick()
//        initSpan()
//        setValues()

        initSpan()
        setCustomContentView()
        timer.start()
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initClick() {
        binding.onClickListener = this
    }

    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")
        val regular = Typeface.createFromAsset(assets, "fonts/inter_regular.ttf")

        val myVitalScanSpan = Spannable.Factory.getInstance().newSpannable(getString(R.string.welcome_to_myvitalscan))
        val mySpan: CustomTypefaceSpan = CustomTypefaceSpan(regular)
        myVitalScanSpan.setSpan(mySpan, 11, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        val vitalsSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        myVitalScanSpan.setSpan(vitalsSpan, 13, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val canSpan: CustomTypefaceSpan = CustomTypefaceSpan(regular)
        myVitalScanSpan.setSpan(canSpan, 19, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.tvWelcome.text = myVitalScanSpan
        binding.tvWelcome.movementMethod = LinkMovementMethod.getInstance()
        binding.tvWelcome1.text = myVitalScanSpan
        binding.tvWelcome1.movementMethod = LinkMovementMethod.getInstance()


        val thisIsLimitedStr = getString(R.string.this_is_a_limited_version_for_demonstration_purposes)
        val thisIsLimitedSpan = Spannable.Factory.getInstance().newSpannable(thisIsLimitedStr)
        val vitalsSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        thisIsLimitedSpan.setSpan(vitalsSpan1, (thisIsLimitedStr.length - 23), (thisIsLimitedStr.length - 17), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvThisIsLimited.text = thisIsLimitedSpan
        binding.tvThisIsLimited.movementMethod = LinkMovementMethod.getInstance()
        binding.tvThisIsLimited1.text = thisIsLimitedSpan
        binding.tvThisIsLimited1.movementMethod = LinkMovementMethod.getInstance()

    }


    private fun setCustomContentView() {
        if (Pref.isLowView) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 0
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cl_transaction, R.id.cl_welcome, R.id.cl_welcome_r, R.id.tv_welcome, R.id.tv_welcome1,
            R.id.tv_this_is_limited, R.id.tv_this_is_limited1, R.id.iv_flower, R.id.iv_flower1 -> {
                timer.cancel()
                PrivacyPolicyTermsConditionActivity.startActivity(this@TransactionActivity)
            }
        }
    }

    private val timer = object : CountDownTimer(8000, 1000) {
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            PrivacyPolicyTermsConditionActivity.startActivity(this@TransactionActivity)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
//            setResultOk()
        }
    }


}