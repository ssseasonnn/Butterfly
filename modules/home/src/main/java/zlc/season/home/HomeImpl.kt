package zlc.season.home

import androidx.fragment.app.Fragment
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl("home", singleton = true)
class HomeImpl : Home {
    var homeFragment: HomeFragment? = null

    override fun create(): Fragment {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        return homeFragment!!
    }
}