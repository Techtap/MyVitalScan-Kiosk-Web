package com.techtap.utils

import android.app.Activity
import com.techtap.R

object AnimationsHandler {
    enum class Animations {
        LeftToRight, RightToLeft, UpToDown, NoAnimation, NoChangeToDown, RightToNoChange, UpToNoChange, FadeEnter, FadeExit
    }

    fun playActivityAnimation(activity: Activity?, animation: Animations) {
        when (animation) {
            Animations.FadeEnter -> {
                activity?.overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit)
            }

            Animations.FadeExit -> {
                activity?.overridePendingTransition(R.anim.animate_fade_exit, R.anim.animate_fade_enter)
            }

            Animations.LeftToRight -> {
                activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            Animations.RightToLeft -> {
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            Animations.RightToNoChange -> {
                activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.no_change)
            }

            Animations.UpToDown -> {
                activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)
            }

            Animations.UpToNoChange -> {
                activity?.overridePendingTransition(R.anim.slide_in_up, R.anim.no_change)
            }

            Animations.NoChangeToDown -> {
                activity?.overridePendingTransition(R.anim.no_change, R.anim.slide_out_down)
            }

            Animations.NoAnimation -> {
                activity?.overridePendingTransition(0, 0)
            }
        }
    }

    fun playBaseActivityAnimation(activity: Activity?) {
        playActivityAnimation(activity, getFinishAnimation(activity))
    }

    private fun getFinishAnimation(activity: Activity?): Animations {
//        return Animations.LeftToRight
        return Animations.FadeEnter
    }

}