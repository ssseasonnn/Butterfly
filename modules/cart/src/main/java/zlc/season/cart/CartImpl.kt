package zlc.season.cart

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl
class CartImpl {
    var cartFragment: CartFragment? = null

    fun showCart(fragmentManager: FragmentManager, container: Int) {
        if (cartFragment == null) {
            cartFragment = CartFragment()
        }
        cartFragment?.let {
            fragmentManager.beginTransaction()
                .replace(container, it, "tag")
                .commit()
        }
    }
}