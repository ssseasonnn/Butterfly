package zlc.season.user

import androidx.fragment.app.Fragment
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl("user", singleton = true)
class UserImpl {
    var userFragment: UserFragment? = null

    fun create(): Fragment {
        if (userFragment == null) {
            userFragment = UserFragment()
        }
        return userFragment!!
    }

}