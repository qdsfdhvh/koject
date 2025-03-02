package com.moriatsushi.koject.integrationtest.app.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.moriatsushi.koject.Koject
import com.moriatsushi.koject.android.activity.injectViewModels
import com.moriatsushi.koject.android.fragment.injectActivityViewModels
import com.moriatsushi.koject.start
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentActivityViewModelTest {
    @After
    fun clear() {
        Koject.stop()
    }

    @Test
    fun injectActivityViewModel() {
        Koject.start()

        val scenario = launchActivity<FragmentActivity>()
        val fragment = Fragment()
        var viewModelByActivity: SampleViewModel? = null
        var viewModelByFragment: SampleViewModel? = null

        scenario.onActivity {
            viewModelByActivity = it.injectViewModels<SampleViewModel>().value
            it.supportFragmentManager.commitNow {
                add(fragment, "tag")
            }
            viewModelByFragment = fragment.injectActivityViewModels<SampleViewModel>().value
        }

        assertNotNull(viewModelByActivity)
        assertNotNull(viewModelByFragment)
        assertSame(viewModelByActivity, viewModelByFragment)
    }
}
