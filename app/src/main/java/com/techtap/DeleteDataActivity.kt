package com.techtap

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityDeleteDataBinding
import com.techtap.databinding.ActivityDeleteDataRBinding
import com.techtap.databinding.ActivityLeaveSessionBinding
import com.techtap.databinding.ActivityLeaveSessionRBinding
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
class DeleteDataActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ViewDataBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var scanId: String? = null

    companion object {
        fun startActivity(activity: Activity, scanId: String?) {
            Intent(activity, DeleteDataActivity::class.java).apply {
                putExtra(Constant.IntentConstant.ID, scanId)
            }.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_delete_data)
        init()
        initObserver()
        playPrompt()
    }

    private fun init() {
        scanId = intent.getStringExtra(Constant.IntentConstant.ID)
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.deleteScanIdState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null) {
                            Utils.showSuccessMessage(this@DeleteDataActivity, it.data.message)
                            Pref.generateCodeObject = null
                            Handler(Looper.getMainLooper()).postDelayed({
                                SplashActivity.startActivityClearTop(this@DeleteDataActivity)
                                finish()
                            }, 2000)
                        } else {
                            Utils.showErrorMessage(this@DeleteDataActivity, it.data?.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        Utils.showErrorMessage(this@DeleteDataActivity, it.message)
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
            DataBindingUtil.setContentView(this, R.layout.activity_delete_data_r)
        } else {
            DataBindingUtil.setContentView(this, R.layout.activity_delete_data)
        }
        initClick()
        setPromptIcon()
    }

    private fun initClick() {
        if (binding is ActivityDeleteDataBinding) {
            (binding as ActivityDeleteDataBinding).onClickListener = this
        } else if (binding is ActivityDeleteDataRBinding) {
            (binding as ActivityDeleteDataRBinding).onClickListener = this
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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_delete_result)
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_low_view -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }

            R.id.btn_yes_delete -> {
                onDeleteResultData()
            }

            R.id.btn_stay -> {
                finish()
            }
        }
    }

    private fun onDeleteResultData() {
//        val scanId = Pref.generateCodeObject?.generateCode
//        val scanId = "F12"
        onBoardingViewModel.deleteScanId(scanId)
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            if (binding is ActivityDeleteDataBinding) {
                (binding as ActivityDeleteDataBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            } else if (binding is ActivityDeleteDataRBinding) {
                (binding as ActivityDeleteDataRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            }
        } else {
            if (binding is ActivityDeleteDataBinding) {
                (binding as ActivityDeleteDataBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            } else if (binding is ActivityDeleteDataRBinding) {
                (binding as ActivityDeleteDataRBinding).ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            }
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_delete_result)
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