package zlc.season.home

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.EvadeImpl
import zlc.season.butterfly.compose.AgileComposable

@EvadeImpl
class HomeImpl {
    val TAG = "home_tag"

    var homeFragment: HomeFragment? = null

    fun showHome(fragmentManager: FragmentManager, container: Int) {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        homeFragment?.let {
            fragmentManager.beginTransaction()
                .replace(container, it, TAG)
                .commit()
        }
    }

    fun isHomeShowing(fragmentManager: FragmentManager): Boolean {
        val find = fragmentManager.findFragmentByTag(TAG)
        return find != null
    }

    fun hideHome(fragmentManager: FragmentManager) {
        homeFragment?.let {
            fragmentManager.beginTransaction()
                .remove(it)
                .commit()
        }
    }

    fun testCompose(): AgileComposable {
        return AgileComposable(composable = @Composable {
            BasicText("test compose")
        })
    }
}