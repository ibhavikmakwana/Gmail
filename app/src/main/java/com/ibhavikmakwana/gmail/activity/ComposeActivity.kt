package com.ibhavikmakwana.gmail.activity

import android.R.attr.x
import android.R.attr.y
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import com.ibhavikmakwana.gmail.R
import kotlinx.android.synthetic.main.activity_compose.*


class ComposeActivity : AppCompatActivity() {

    private var revealX: Int? = null
    private var revealY: Int? = null

    companion object {
        private const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
        private const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"

        /**
         * call this method to launch the Main Activity
         */
        fun launchActivity(activity: Activity, view: View) {

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, "transition")
            val revealX = (view.x + view.width / 2).toInt()
            val revealY = (view.y + view.height / 2).toInt()

            val intent = Intent(activity, ComposeActivity::class.java)
            intent.putExtra(EXTRA_CIRCULAR_REVEAL_X, revealX)
            intent.putExtra(EXTRA_CIRCULAR_REVEAL_Y, revealY)

            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {

            root.visibility = View.INVISIBLE

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)


            val viewTreeObserver = root.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX!!, revealY!!)
                        root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            root.visibility = View.VISIBLE
        }
    }

    private fun revealActivity(revealX: Int, revealY: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(root.width, root.height) * 1.1).toFloat()

            // create the animator for this view (the start radius is zero)
            val circularReveal = ViewAnimationUtils.createCircularReveal(root, x, y, 0f, finalRadius)
            circularReveal.duration = 400
            circularReveal.interpolator = AccelerateInterpolator()

            // make the view visible and start the animation
            root.visibility = View.VISIBLE
            circularReveal.start()
        } else {
            finish()
        }
    }

    private fun unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish()
        } else {
            val finalRadius = (Math.max(root.width, root.height) * 1.1).toFloat()
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                    root, this.revealX!!, this.revealY!!, finalRadius, 0f)

            circularReveal.duration = 400
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    root.visibility = View.INVISIBLE
                    finish()
                }
            })


            circularReveal.start()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        unRevealActivity()
    }
}