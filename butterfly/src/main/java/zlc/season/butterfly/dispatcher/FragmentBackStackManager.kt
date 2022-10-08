package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.claritypotion.ClarityPotion
import java.lang.ref.WeakReference

class FragmentBackStackManager {
    private val fragmentBackStackMap = mutableMapOf<Int, MutableList<FragmentEntry>>()

    init {
        ClarityPotion.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                removeBackStack(activity.hashCode())
            }
        })
    }

    @Synchronized
    fun removeBackStack(hashCode: Int) {
        fragmentBackStackMap.remove(hashCode)
    }

    @Synchronized
    fun addEntry(activity: FragmentActivity, entry: FragmentEntry) {
        getBackStackList(activity).add(entry)
    }

    @Synchronized
    fun removeEntry(activity: FragmentActivity, entry: FragmentEntry) {
        val backStackList = getBackStackList(activity)
        backStackList.remove(entry)
    }

    @Synchronized
    fun removeEntries(activity: FragmentActivity, entryList: List<FragmentEntry>) {
        val backStackList = getBackStackList(activity)
        backStackList.removeAll(entryList)
    }

    @Synchronized
    fun getTopEntryList(activity: FragmentActivity, request: AgileRequest): MutableList<FragmentEntry> {
        val result = mutableListOf<FragmentEntry>()

        val backStackList = getBackStackList(activity)
        val index = backStackList.indexOfLast { it.request.className == request.className }
        if (index != -1) {
            for (i in index until backStackList.size) {
                val entry = backStackList[i]
                result.add(entry)
            }
        }
        return result
    }

    @Synchronized
    fun getTopEntry(activity: FragmentActivity): FragmentEntry? {
        val backStackList = getBackStackList(activity)

        return if (backStackList.isEmpty()) {
            null
        } else {
            return backStackList.lastOrNull()
        }
    }

    @Synchronized
    private fun getBackStackList(activity: FragmentActivity): MutableList<FragmentEntry> {
        var backStackList = fragmentBackStackMap[activity.hashCode()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            fragmentBackStackMap[activity.hashCode()] = backStackList
        }

        clearUselessEntry(backStackList)

        return backStackList
    }

    private fun clearUselessEntry(backStackList: MutableList<FragmentEntry>) {
        val shouldRemoveList = mutableListOf<FragmentEntry>()
        backStackList.forEach {
            if (it.reference.get() == null) {
                shouldRemoveList.add(it)
            }
        }
        backStackList.removeAll(shouldRemoveList)
    }

    data class FragmentEntry(val request: AgileRequest, val reference: WeakReference<Fragment>)

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
