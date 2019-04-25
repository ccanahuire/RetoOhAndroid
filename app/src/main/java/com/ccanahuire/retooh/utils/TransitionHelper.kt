package com.ccanahuire.retooh.utils

import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionSet

abstract class TransitionHelper {
    companion object {
        fun createEnterTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.END).setStartDelay(150))
            return transitionSet
        }

        fun createReEnterTransition(): Transition {
            val transitionSet = TransitionSet()
            transitionSet.addTransition(Slide(GravityCompat.START).setStartDelay(150))
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

        fun setDefaultTransition(fragment: Fragment) {
            fragment.enterTransition = createEnterTransition()
            fragment.reenterTransition = createReEnterTransition()
            fragment.exitTransition = createExitTransition()
            fragment.returnTransition = createReturnTransition()
        }

    }
}