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
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techtap.base.BaseActivity
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionBinding
import com.techtap.databinding.ActivityPrivacyPolicyTermsConditionRBinding
import com.techtap.databinding.ActivityQuestionBinding
import com.techtap.network.Status
import com.techtap.utils.Constant
import com.techtap.utils.CustomTypefaceSpan
import com.techtap.utils.Logger
import com.techtap.utils.Pref
import com.techtap.utils.Utils
import com.techtap.viewmodel.OnBoardingViewModel
import kotlinx.coroutines.launch
import java.security.AccessController.getContext


class QuestionActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQuestionBinding
    private var mediaPlayer: MediaPlayer? = null
    private val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private val listItems: ArrayList<Question> = arrayListOf()
    private var adapter: QuestionAdapter? = null

    companion object {
        fun startActivity(activity: Activity) {
            Intent(activity, QuestionActivity::class.java).run {
                activity.startActivity(this)
//                AnimationsHandler.playActivityAnimation(activity, AnimationsHandler.Animations.FadeEnter)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question)

        initSpan()
        setCustomContentView()
        initAdapter()
        initClick()
        initObserver()

        setPromptIcon()
        playPrompt()

        onBoardingViewModel.getQuestion(Pref.generateCodeObject?.generateCode)
//        onBoardingViewModel.getQuestion("X34")

    }

    private fun initSpan() {
        val bold = Typeface.createFromAsset(assets, "fonts/inter_bold.ttf")

        val impactStr = getString(R.string.impact)

        val keepingYourBodyStr = getString(R.string.keeping_your_body_hydrated)
        val keepingYourBodySpan = Spannable.Factory.getInstance().newSpannable("$impactStr $keepingYourBodyStr")
        val impactSpan: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        keepingYourBodySpan.setSpan(impactSpan, 0, impactStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        keepingYourBodySpan.setSpan(StyleSpan(Typeface.ITALIC), 0, impactStr.length, 0);
        binding.tvHowMuchWaterDesc.text = keepingYourBodySpan
        binding.tvHowMuchWaterDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvHowMuchWaterDesc1.text = keepingYourBodySpan
        binding.tvHowMuchWaterDesc1.movementMethod = LinkMovementMethod.getInstance()


        val someHealthConditionStr = getString(R.string.some_health_conditions_are_linked)
        val someHealthConditionSpan = Spannable.Factory.getInstance().newSpannable("$impactStr $someHealthConditionStr")
        val impactSpan1: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        someHealthConditionSpan.setSpan(impactSpan1, 0, impactStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        someHealthConditionSpan.setSpan(StyleSpan(Typeface.ITALIC), 0, impactStr.length, 0);
        binding.tvHowOftenFeelTiredDesc.text = someHealthConditionSpan
        binding.tvHowOftenFeelTiredDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvHowOftenFeelTiredDesc1.text = someHealthConditionSpan
        binding.tvHowOftenFeelTiredDesc1.movementMethod = LinkMovementMethod.getInstance()

        val understandingYourSenseStr = getString(R.string.understanding_your_sense_of_purpose)
        val understandingYourSenseSpan = Spannable.Factory.getInstance().newSpannable("$impactStr $understandingYourSenseStr")
        val impactSpan2: CustomTypefaceSpan = CustomTypefaceSpan(bold)
        understandingYourSenseSpan.setSpan(impactSpan2, 0, impactStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        understandingYourSenseSpan.setSpan(StyleSpan(Typeface.ITALIC), 0, impactStr.length, 0);
        binding.tvRateStatementDesc.text = understandingYourSenseSpan
        binding.tvRateStatementDesc.movementMethod = LinkMovementMethod.getInstance()
        binding.tvRateStatementDesc1.text = understandingYourSenseSpan
        binding.tvRateStatementDesc1.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun setCustomContentView() {
        if (Pref.isLowView) {
            binding.viewFlipper.displayedChild = 1
        } else {
            binding.viewFlipper.displayedChild = 0
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            onBoardingViewModel.getQuestionState.collect {
                when (it.status) {
                    Status.LOADING -> {
//                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
//                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null) {
                            if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null && it.data.list != null) {
                                listItems.addAll(it.data.list!!)
                            }
                            adapter!!.notifyDataSetChanged()
                        } else {
                            Utils.showErrorMessage(this@QuestionActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
//                        showHideProgress(false)
                        Utils.showErrorMessage(this@QuestionActivity, it.message)
                    }
                }
            }
        }


        lifecycleScope.launch {
            onBoardingViewModel.addQuestionState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        showHideProgress(it.data == null)
                    }

                    Status.SUCCESS -> {
                        showHideProgress(false)
                        if (it.code == Constant.StatusCodes.STATUS_CODE_SUCCESS && it.data != null) {
                            PersonaSelectionActivity.startActivity(this@QuestionActivity)
                        } else {
                            Utils.showErrorMessage(this@QuestionActivity, it.message)
                        }
                    }

                    Status.ERROR -> {
                        showHideProgress(false)
                        Utils.showErrorMessage(this@QuestionActivity, it.message)
                    }
                }
            }
        }


    }

    private fun initAdapter() {
//        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val layoutManager: LinearLayoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.rvQuestion.layoutManager = layoutManager
        adapter = QuestionAdapter(this, listItems)
        binding.rvQuestion.adapter = adapter


//        val layoutManager1: RecyclerView.LayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val layoutManager1: LinearLayoutManager = object : LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.rvQuestion1.layoutManager = layoutManager1
        binding.rvQuestion1.adapter = adapter


        adapter!!.setItemClickListener(object : QuestionAdapter.QuestionListener {
            override fun onQuestionOptionClick(questionOption: QuestionOption?, childPosition: Int) {
                Logger.e("parentPosition = ${questionOption?.questionId} childPosition = $childPosition")

                var selectedPosition = -1
                for (i in listItems.indices) {
                    if (listItems[i].id == questionOption?.questionId) {
                        selectedPosition = i
                        val optionList = listItems[i].options
                        for (j in optionList?.indices!!) {
                            optionList[j].selected = optionList[j].id == questionOption.id
                        }
                    }
                }
                adapter!!.notifyChildAdapter(selectedPosition, childPosition)

                setContinueAlpha()

            }
        })
    }

    private fun initClick() {
        binding.onClickListener = this

        /*binding.rgHowMuchWater.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_less_than_1_ltr -> {
                    if (!Pref.isLowView) {
                        binding.rgHowMuchWater1.check(R.id.rb_less_than_1_ltr1)
                    }
                }

                R.id.rb_between_1_and_2_ltr -> {
                    if (!Pref.isLowView) {
                        binding.rgHowMuchWater1.check(R.id.rb_between_1_and_2_ltr1)
                    }

                }

                R.id.rb_more_than_2_ltr -> {
                    if (!Pref.isLowView) {
                        binding.rgHowMuchWater1.check(R.id.rb_more_than_2_ltr1)
                    }

                }
            }
            setContinueAlpha()
        }

        binding.rgHowMuchWater1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_less_than_1_ltr1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowMuchWater.check(R.id.rb_less_than_1_ltr)
                    }
                }

                R.id.rb_between_1_and_2_ltr1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowMuchWater.check(R.id.rb_between_1_and_2_ltr)
                    }
                }

                R.id.rb_more_than_2_ltr1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowMuchWater.check(R.id.rb_more_than_2_ltr)
                    }
                }
            }
            setContinueAlpha()
        }

        binding.rgHowOftenFeelTired.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_never -> {
                    if (!Pref.isLowView) {
                        binding.rgHowOftenFeelTired1.check(R.id.rb_never1)
                    }
                }

                R.id.rb_once_a_week -> {
                    if (!Pref.isLowView) {
                        binding.rgHowOftenFeelTired1.check(R.id.rb_once_a_week1)
                    }

                }

                R.id.rb_1_to_2_times_a_week -> {
                    if (!Pref.isLowView) {
                        binding.rgHowOftenFeelTired1.check(R.id.rb_1_to_2_times_a_week1)
                    }

                }

                R.id.rb_more_than_3_times_a_week -> {
                    if (!Pref.isLowView) {
                        binding.rgHowOftenFeelTired1.check(R.id.rb_more_than_3_times_a_week1)
                    }
                }
            }
            setContinueAlpha()
        }

        binding.rgHowOftenFeelTired1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_never1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowOftenFeelTired.check(R.id.rb_never)
                    }
                }

                R.id.rb_once_a_week1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowOftenFeelTired.check(R.id.rb_once_a_week)
                    }

                }

                R.id.rb_1_to_2_times_a_week1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowOftenFeelTired.check(R.id.rb_1_to_2_times_a_week)
                    }

                }

                R.id.rb_more_than_3_times_a_week1 -> {
                    if (Pref.isLowView) {
                        binding.rgHowOftenFeelTired.check(R.id.rb_more_than_3_times_a_week)
                    }
                }
            }

            setContinueAlpha()
        }

        binding.rgRateStatement.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_disagree -> {
                    if (!Pref.isLowView) {
                        binding.rgRateStatement1.check(R.id.rb_disagree1)
                    }
                }

                R.id.rb_neutral -> {
                    if (!Pref.isLowView) {
                        binding.rgRateStatement1.check(R.id.rb_neutral1)
                    }

                }

                R.id.rb_agree -> {
                    if (!Pref.isLowView) {
                        binding.rgRateStatement1.check(R.id.rb_agree1)
                    }

                }
            }
            setContinueAlpha()
        }
        binding.rgRateStatement1.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_disagree1 -> {
                    if (Pref.isLowView) {
                        binding.rgRateStatement.check(R.id.rb_disagree)
                    }
                }

                R.id.rb_neutral1 -> {
                    if (Pref.isLowView) {
                        binding.rgRateStatement.check(R.id.rb_neutral)
                    }

                }

                R.id.rb_agree1 -> {
                    if (Pref.isLowView) {
                        binding.rgRateStatement.check(R.id.rb_agree)
                    }

                }
            }
            setContinueAlpha()
        }*/
    }

    private fun setContinueAlpha() {
//        if (Pref.isLowView) {
//            val rgHowMuchWaterId = binding.rgHowMuchWater1.checkedRadioButtonId
//            val rgHowOftenFeelTiredId = binding.rgHowOftenFeelTired1.checkedRadioButtonId
//            val rgRateStatementId = binding.rgRateStatement1.checkedRadioButtonId
//
//            if (rgHowMuchWaterId != -1 && rgHowOftenFeelTiredId != -1 && rgRateStatementId != -1) {
//                binding.btnContinue1.alpha = 1f
//            } else {
//                binding.btnContinue1.alpha = 0.5f
//            }
//        } else {
//            val rgHowMuchWaterId = binding.rgHowMuchWater.checkedRadioButtonId
//            val rgHowOftenFeelTiredId = binding.rgHowOftenFeelTired.checkedRadioButtonId
//            val rgRateStatementId = binding.rgRateStatement.checkedRadioButtonId
//
//            if (rgHowMuchWaterId != -1 && rgHowOftenFeelTiredId != -1 && rgRateStatementId != -1) {
//                binding.btnContinue.alpha = 1f
//            } else {
//                binding.btnContinue.alpha = 0.5f
//            }
//        }

        var selectedCount = 0
        for (i in listItems.indices) {
            val optionList = listItems[i].options
            for (j in optionList?.indices!!) {
                if (optionList[j].selected) {
                    selectedCount += 1
                    break
                }
            }
        }

        if (listItems.size == selectedCount) {
            binding.btnContinue.alpha = 1f
            binding.btnContinue1.alpha = 1f
        } else {
            binding.btnContinue.alpha = 0.5f
            binding.btnContinue1.alpha = 0.5f
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.iv_play_sound, R.id.iv_play_sound1 -> {
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
//                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_questionnaire)
//                    mediaPlayer?.start()
//                }
            }

            R.id.iv_back, R.id.iv_back1 -> {
                finish()
            }

            R.id.iv_exit, R.id.iv_exit1 -> {
                SplashActivity.startActivityClearTop(this)
                finish()
            }

            R.id.iv_low_view, R.id.iv_low_view1 -> {
                Pref.isLowView = !Pref.isLowView
                setCustomContentView()
            }

            R.id.btn_skip, R.id.btn_skip1 -> {
                PersonaSelectionActivity.startActivity(this)
            }

            R.id.btn_continue, R.id.btn_continue1 -> {
//                PersonaSelectionActivity.startActivity(this)


                val addQuestionList: ArrayList<AddQuestion> = arrayListOf()
                for (i in listItems.indices) {
                    val optionList = listItems[i].options
                    for (j in optionList?.indices!!) {
                        if (optionList[j].selected) {
                            addQuestionList.add(AddQuestion(optionList[j].questionId, optionList[j].id))
                            break
                        }
                    }
                }

                if (addQuestionList.size == listItems.size) {
                    val addQuestionRequest = AddQuestionRequest(addQuestionList)
                    onBoardingViewModel.addQuestion(Pref.generateCodeObject?.generateCode, addQuestionRequest)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopPrompt()
    }

    private fun setPromptIcon() {
        if (Pref.isAudioOn) {
            binding.ivPlaySound.setImageResource(R.drawable.ic_prompt_on)
            binding.ivPlaySound1.setImageResource(R.drawable.ic_prompt_on)
        } else {
            binding.ivPlaySound.setImageResource(R.drawable.ic_prompt_off)
            binding.ivPlaySound1.setImageResource(R.drawable.ic_prompt_off)
        }
    }

    private fun playPrompt() {
        if (Pref.isAudioOn && mediaPlayer?.isPlaying != true) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    mediaPlayer = MediaPlayer()
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.audio_questionnaire)
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