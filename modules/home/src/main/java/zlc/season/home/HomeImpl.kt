package zlc.season.home

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl
class HomeImpl : Home {
    var homeFragment: HomeFragment? = null

    override fun showHome(fragmentManager: FragmentManager, container: Int) {
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