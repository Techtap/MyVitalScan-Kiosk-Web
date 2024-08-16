package com.techtap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityFullReportBinding
import com.techtap.utils.Constant
import com.techtap.utils.DateFormatter
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FullReportActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun startActivity(activity: Activity, measurementDetail: MeasurementResponse?) {
            Intent(activity, FullReportActivity::class.java).apply {
                putExtra(Constant.IntentConstant.SCAN_RESULT, measurementDetail)
            }.run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    private lateinit var binding: ActivityFullReportBinding
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private val listItems: ArrayList<Any> = arrayListOf()
    private val listItemsReverse: ArrayList<Any> = arrayListOf()
    private var measurementDetail: MeasurementResponse? = null
    private var scanId: String? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isExternal: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_report)
        init()
        setCustomContentView()
        initObserver()
        initClick()
        initAdapter()
        setValue()
        setPromptIcon()
        playPrompt()
    }

    private fun init() {
        measurementDetail = Utils.getParcelable(intent, Constant.IntentConstant.SCAN_RESULT, MeasurementResponse::class.java)
        isExternal = measurementDetail?.basicInfo?.isExternal!!
    }

    private fun initObserver() {

    }

    private fun initClick() {
        binding.onClickListener = this
    }

    private fun initAdapter() {
        binding.adapter = ReportListAdapter(listItems, isExternal).apply {
            reportListener = object : ReportListAdapter.ReportListener {
                override fun onReportItemClick(reading: Reading?, position: Int) {
                    reading?.isSelected = !reading?.isSelected!!
                    binding.adapter!!.notifyItemChanged(position)
                }
            }
        }


//        binding.adapterReverse = ReportListAdapterReverse(listItemsReverse).apply {
//            reportListener = object : ReportListAdapterReverse.ReportListener {
//                override fun onReportItemClick(reading: Reading?, position: Int) {
//                    reading?.isSelected = !reading?.isSelected!!
//                    binding.adapter!!.notifyItemChanged(position)
//                }
//            }
//        }
//        binding.viewPager.adapter = binding.adapterReverse!!


        val helper: SnapHelper = LinearSnapHelper()

        binding.rvReportListR.apply {
//            addItemDecoration(SimpleDividerItemDecoration1())
//            helper.attachToRecyclerView(this)
        }

        binding.adapterReverse = ReportListAdapterReverse(listItemsReverse).apply {
            reportListener = object : ReportListAdapterReverse.ReportListener {
                override fun onReportItemClick(reading: Reading?, position: Int) {
                    reading?.isSelected = !reading?.isSelected!!
                    binding.rvReportListR.smoothScrollToPosition(position)
//                    binding.adapterReverse!!.notifyItemChanged(position)
                    binding.adapterReverse!!.notifyDataSetChanged()

                }
            }
        }


//        val adapter = ReportListAdapterReverse1(this, listItemsReverse)
//        binding.viewPager.adapter = adapter
////        binding.viewPager.currentItem = position

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setValue() {
//        for (i in 0..10) {
//            listItems.add(Reading())
//        }
//        binding.adapter!!.notifyDataSetChanged()


//        if (Pref.isLowView) {
//
//        } else {
//
//        }

//        binding.ivFlower.setImageResource(Utils.getWellnessImage(this, wellnessScore))


        if (measurementDetail?.result != null) {

            if (BuildConfig.FLAVOR.equals(Constant.INTERNAL, true)) {
                binding.btnExplore.visibility = View.VISIBLE
                binding.btnExplore1.visibility = View.VISIBLE
                binding.btnMyCare.visibility = View.VISIBLE
                binding.btnMyCare1.visibility = View.VISIBLE
            } else {
                binding.btnExplore.visibility = View.INVISIBLE
                binding.btnExplore1.visibility = View.INVISIBLE
                binding.btnMyCare.visibility = View.INVISIBLE
                binding.btnMyCare1.visibility = View.INVISIBLE
            }

            scanId = measurementDetail?.basicInfo?.generateCode

            binding.tvScanId.text = String.format(getString(R.string.scan_id_x), scanId)
            binding.tvScanId1.text = String.format(getString(R.string.scan_id_x), scanId)

//            binding.tvWellnessScore.text = measurementDetail?.basicInfo?.wellnessScore.toString() + "/10"
//            binding.tvWellnessScore1.text = measurementDetail?.basicInfo?.wellnessScore.toString() + "/10"


            val formattedDate = DateFormatter.getFormattedDate(DateFormatter.SERVER_DATE_FORMAT, measurementDetail?.basicInfo?.generatedAt!!, DateFormatter.MMM_dd_hh_mm_a)
            binding.tvDateTime.text = formattedDate
            binding.tvDateTime1.text = formattedDate

            val result = measurementDetail!!.result!!
            if (!result.vitalSigns.isNullOrEmpty()) {
                listItems.addAll(result.vitalSigns!!)
                listItemsReverse.addAll(result.vitalSigns!!)
            }
            if (!result.blood.isNullOrEmpty()) {
                listItems.addAll(result.blood!!)
                listItemsReverse.addAll(result.blood!!)
            }
            if (!result.stressLevel.isNullOrEmpty()) {
                listItems.addAll(result.stressLevel!!)
                listItemsReverse.addAll(result.stressLevel!!)
            }
            if (!result.energy.isNullOrEmpty()) {
                listItems.addAll(result.energy!!)
                listItemsReverse.addAll(result.energy!!)
            }
            if (!result.heartRateVariability.isNullOrEmpty()) {
                listItems.addAll(result.heartRateVariability!!)
                listItemsReverse.addAll(result.heartRateVariability!!)
            }
            if (!result.bloodTest.isNullOrEmpty()) {
                listItems.addAll(result.bloodTest!!)
                listItemsReverse.addAll(result.bloodTest!!)
            }

            if (!result.temperature.isNullOrEmpty()) {
                listItems.addAll(result.temperature!!)
                listItemsReverse.addAll(result.temperature!!)
            }

            if (isExternal == 1) {
                listItemsReverse.add("")
            }
            listItems.add(ReadingDesc())
        }
        binding.adapter!!.notifyDataSetChanged()
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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_full_report)
//                    mediaPlayer?.start()
//                }
            }

            R.id.btn_explore, R.id.btn_explore1 -> {
                LearnMoreActivity.startActivity(this)
            }

            R.id.btn_my_care, R.id.btn_my_care1 -> {
                ShareWithUsActivity.startActivity(this@FullReportActivity, measurementDetail)
            }

            R.id.btn_my_delete_data, R.id.btn_my_delete_data1 -> {
                DeleteDataActivity.startActivity(this, measurementDetail?.basicInfo?.generateCode)
            }

            R.id.iv_back, R.id.iv_back1 -> {
                finish()
            }

            R.id.iv_exit, R.id.iv_exit1 -> {
//                SplashActivity.startActivityClearTop(this)
                LeaveSessionActivity.startActivity(this, measurementDetail?.basicInfo?.generateCode)
            }

            R.id.iv_low_view, R.id.iv_low_view1 -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }
        }
    }

    private fun setCustomContentView() {
        if (Pref.isLowView) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 0
        }
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            binding.ivPlaySound.setImageResource(R.drawable.ic_prompt_on_white)
            binding.ivPlaySound1.setImageResource(R.drawable.ic_prompt_on)
        } else {
            binding.ivPlaySound.setImageResource(R.drawable.ic_prompt_off_white)
            binding.ivPlaySound1.setImageResource(R.drawable.ic_prompt_off)
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_full_report)
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