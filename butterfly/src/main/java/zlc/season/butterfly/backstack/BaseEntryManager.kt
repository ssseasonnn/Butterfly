package zlc.season.butterfly.backstack

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion

abstract class BaseEntryManager<E : BaseEntry> {
    private val fragmentEntryMap = mutableMapOf<Int, MutableList<E>>()

    init {
        ClarityPotion.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                synchronized(this@BaseEntryManager) {
                    fragmentEntryMap.remove(activity.hashCode())
                }
            }
        })
    }

    @Synchronized
    protected fun getEntryList(activity: FragmentActivity): MutableList<E> {
        var backStackList = fragmentEntryMap[activity.hashCode()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            fragmentEntryMap[activity.hashCode()] = backStackList
        }

        clearUselessEntry(backStackList)

        return backStackList
    }

    private fun clearUselessEntry(backStackList: MutableList<E>) {
        val shouldRemoveList = mutableListOf<E>()
        backStackList.forEach {
            if (it.reference.get() == null) {
                shouldRemoveList.add(it)
            }
        }
        backStackList.removeAll(shouldRemoveList)
    }

    @Synchronized
    fun addEntry(activity: FragmentActivity, entry: E) {
        getEntryList(activity).add(entry)
    }

    @Synchronized
    fun removeEntry(activity: FragmentActivity, entry: E) {
        val backStackList = getEntryList(activity)
        backStackList.remove(entry)
    }

    @Synchronized
    fun getEntrySize(activity: FragmentActivity): Int {
        return getEntryList(activity).size
    }

    @Synchronized
    fun getTopEntry(activity: FragmentActivity): E? {
        val entryList = getEntryList(activity)

        return if (entryList.isEmpty()) {
            null
        } else {
            return entryList.lastOrNull()
        }
    }
}