package zlc.season.cart

import androidx.fragment.app.Fragment
import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl("cart", singleton = true)
class CartImpl {
    var cartFragment: CartFragment? = null

    fun create(): Fragment {
        if (cartFragment == null) {
            cartFragment = CartFragment()
        }
        return cartFragment!!
    }

}