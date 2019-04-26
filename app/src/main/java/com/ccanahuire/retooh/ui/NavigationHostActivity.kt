package com.ccanahuire.retooh.ui

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ccanahuire.retooh.R

abstract class NavigationHostActivity : AppCompatActivity(), NavigationHost {

    protected var lastNavigatedFragment: Fragment? = null

    /**
     * Provide the container id res which fragments will be replaced.
     */
    @IdRes
    abstract fun containerId(): Int

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
        lastNavigatedFragment = fragment
    }

    override fun navigateBack() {
        supportFragmentManager.popBackStack()
    }
}