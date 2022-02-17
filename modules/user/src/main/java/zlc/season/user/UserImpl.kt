package zlc.season.user

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl
class UserImpl {
    var userFragment: UserFragment? = null

    fun showUser(fragmentManager: FragmentManager, container: Int) {
        if (userFragment == null) {
            userFragment = UserFragment()
        }
        userFragment?.let {
            fragmentManager.beginTransaction()
                .replace(container, it, "tag")
                .commit()
        }
    }
}