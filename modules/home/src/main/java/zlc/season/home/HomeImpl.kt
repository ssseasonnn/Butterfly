package zlc.season.home

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl
class HomeImpl {
    var homeFragment: HomeFragment? = null

    fun showHome(fragmentManager: FragmentManager, container: Int) {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        homeFragment?.let {
            fragmentManager.beginTransaction()
                .replace(container, it, "tag")
                .commit()
        }
    }
}