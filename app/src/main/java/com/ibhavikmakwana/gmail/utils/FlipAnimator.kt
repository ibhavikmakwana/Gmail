package com.ibhavikmakwana.gmail.utils

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View

import com.ibhavikmakwana.gmail.R

class FlipAnimator {
    private var leftIn: AnimatorSet? = null
    private var rightOut: AnimatorSet? = null
    private var leftOut: AnimatorSet? = null
    private var rightIn: AnimatorSet? = null

    /**
     * Performs flip animation on two views
     */
    fun flipView(context: Context, back: View, front: View, showFront: Boolean) {
        leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in) as AnimatorSet
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out) as AnimatorSet
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out) as AnimatorSet
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in) as AnimatorSet

        val showFrontAnim = AnimatorSet()
        val showBackAnim = AnimatorSet()

        leftIn!!.setTarget(back)
        rightOut!!.setTarget(front)
        showFrontAnim.playTogether(leftIn, rightOut)

        leftOut!!.setTarget(back)
        rightIn!!.setTarget(front)
        showBackAnim.playTogether(rightIn, leftOut)

        if (showFront) {
            showFrontAnim.start()
        } else {
            showBackAnim.start()
        }
    }
}