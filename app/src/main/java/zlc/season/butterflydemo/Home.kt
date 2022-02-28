package zlc.season.butterflydemo

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.Evade

@Evade
interface Home {
    fun isHomeShowing(fragmentManager: FragmentManager): Boolean
    fun showHome(fragmentManager: FragmentManager, container: Int)
    fun hideHome(fragmentManager: FragmentManager)
}