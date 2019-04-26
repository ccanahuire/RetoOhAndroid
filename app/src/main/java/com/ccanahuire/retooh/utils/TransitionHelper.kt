package com.ccanahuire.retooh.utils

import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionSet

abstract class TransitionHelper {
    companion object {
        fun createEnterTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.END).setStartDelay(180))
            return transitionSet
        }

        fun createReEnterTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.START).setStartDelay(180))
            return transitionSet
        }

        fun createExitTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.START))
            return transitionSet
        }

        fun createReturnTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.END))
            return transitionSet
        }

        fun createFadeInTransition(): Transition {
            return Fade(Fade.IN)
        }

        fun createFadeOutTransition(): Transition {
            return Fade(Fade.OUT)
        }

        fun setDefaultTransition(fragment: Fragment) {
            fragment.enterTransition = createEnterTransition()
            fragment.reenterTransition = createReEnterTransition()
            fragment.exitTransition = createExitTransition()
            fragment.returnTransition = createReturnTransition()
        }

    }
}