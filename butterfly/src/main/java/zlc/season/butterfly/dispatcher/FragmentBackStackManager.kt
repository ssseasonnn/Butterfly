package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.Butterfly.setResult
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.claritypotion.ClarityPotion

class FragmentBackStackManager {
    private val fragmentBackStackMap = mutableMapOf<Int, MutableList<FragmentEntry>>()

    init {
        ClarityPotion.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                synchronized(this@FragmentBackStackManager) {
                    if (fragmentBackStackMap[activity.hashCode()] != null) {
                        fragmentBackStackMap.remove(activity.hashCode())
                    }
                }
            }
        })
    }

    fun pushFragment(activity: FragmentActivity, fragment: Fragment, request: AgileRequest) {
        getBackStackList(activity).add(FragmentEntry(request, fragment))
    }

    fun popFragment(activity: FragmentActivity, request: AgileRequest) {
        val fragmentManager = activity.supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(request.fragmentConfig.tag)
        if (fragment != null) {
            synchronized(this) {
                val backStackList = getBackStackList(activity)
                if (backStackList.isNotEmpty()) {
                    val index = backStackList.indexOfLast { it.request.className == request.className }
                    if (index != -1) {
                        backStackList.removeAt(index)
                    }
                }
            }

            fragment.setResult(request.bundle)
            fragmentManager.remove(fragment)
        }
    }

    @Synchronized
    fun getBackStackList(activity: FragmentActivity): MutableList<FragmentEntry> {
        var backStackList = fragmentBackStackMap[activity.hashCode()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            fragmentBackStackMap[activity.hashCode()] = backStackList
        }
        return backStackList
    }

    data class FragmentEntry(val request: AgileRequest, val fragment: Fragment)

    open class ActivityLifecycleCallbacksAdapter : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {

        }
    }
}
