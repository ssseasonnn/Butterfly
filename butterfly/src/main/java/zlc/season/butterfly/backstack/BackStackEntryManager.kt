package zlc.season.butterfly.backstack

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion
import zlc.season.claritypotion.ClarityPotion.application

class BackStackEntryManager {
    private val backStackEntryMap = mutableMapOf<Int, MutableList<BackStackEntry>>()

    companion object {
        private const val KEY_SAVE_STATE = "butterfly_back_stack_state"
    }

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (savedInstanceState != null) {
                    val data = savedInstanceState.getParcelableArrayList<AgileRequest>(KEY_SAVE_STATE)
                    if (data != null) {
                        synchronized(this@BackStackEntryManager) {
                            val result = data.map { BackStackEntry(it) }
                            getEntryList(activity).addAll(result)
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                synchronized(this@BackStackEntryManager) {
                    val list = backStackEntryMap[activity.hashCode()]
                    if (!list.isNullOrEmpty()) {
                        val savedData = list.mapTo(ArrayList()) { it.request }
                        outState.putParcelableArrayList(KEY_SAVE_STATE, savedData)
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                synchronized(this@BackStackEntryManager) {
                    backStackEntryMap.remove(activity.hashCode())
                }
            }
        })
    }

    @Synchronized
    fun getEntryList(activity: Activity): MutableList<BackStackEntry> {
        var backStackList = backStackEntryMap[activity.hashCode()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            backStackEntryMap[activity.hashCode()] = backStackList
        }

        return backStackList
    }

    @Synchronized
    fun addEntry(activity: FragmentActivity, entry: BackStackEntry) {
        getEntryList(activity).add(entry)
    }

    @Synchronized
    fun removeEntry(activity: FragmentActivity, entry: BackStackEntry) {
        val backStackList = getEntryList(activity)
        backStackList.remove(entry)
    }

    @Synchronized
    fun getEntrySize(activity: FragmentActivity): Int {
        return getEntryList(activity).size
    }

    @Synchronized
    fun getTopEntry(activity: FragmentActivity): BackStackEntry? {
        val entryList = getEntryList(activity)

        return if (entryList.isEmpty()) {
            null
        } else {
            return entryList.lastOrNull()
        }
    }

    @Synchronized
    fun removeEntries(activity: FragmentActivity, entryList: List<BackStackEntry>) {
        val backStackList = getEntryList(activity)
        backStackList.removeAll(entryList)
    }

    @Synchronized
    fun getTopEntryList(activity: FragmentActivity, request: AgileRequest): MutableList<BackStackEntry> {
        val result = mutableListOf<BackStackEntry>()

        val backStackList = getEntryList(activity)
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
    fun findEntry(activity: FragmentActivity, block: (BackStackEntry) -> Boolean): BackStackEntry? {
        val entryList = getEntryList(activity)
        return entryList.lastOrNull(block)
    }
}