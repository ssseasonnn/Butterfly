package zlc.season.butterflydemo

import androidx.fragment.app.FragmentManager
import zlc.season.butterfly.annotation.Evade
import zlc.season.butterfly.compose.AgileComposable

@Evade
interface Home {
    fun isHomeShowing(fragmentManager: FragmentManager): Boolean
    fun showHome(fragmentManager: FragmentManager, container: Int)
    fun hideHome(fragmentManager: FragmentManager)

    fun testCompose(): AgileComposable
}