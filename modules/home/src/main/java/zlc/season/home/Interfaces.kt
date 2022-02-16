package zlc.season.home

import androidx.fragment.app.Fragment
import zlc.season.butterfly.annotation.Evade

interface FragmentCreator {
    fun create(): Fragment
}


@Evade("home")
interface Home : FragmentCreator {
}

@Evade("cart")
interface Cart : FragmentCreator {
}

@Evade("user")
interface User : FragmentCreator {
}