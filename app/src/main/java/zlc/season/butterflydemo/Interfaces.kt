package zlc.season.butterflydemo

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.Evade

@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}

@Evade
interface Cart {
    fun showCart(fragmentManager: FragmentManager, container: Int)
}

@Evade
interface User {
    fun showUser(fragmentManager: FragmentManager, container: Int)
}