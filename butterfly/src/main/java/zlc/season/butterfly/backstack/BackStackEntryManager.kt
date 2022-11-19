package zlc.season.butterfly.backstack

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.Butterfly
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class BackStackEntryManager {
    companion object {
        private const val KEY_SAVE_STATE = "butterfly_back_stack_state"
    }

    private val backStackEntryMap = mutableMapOf<Int, MutableList<BackStackEntry>>()

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            @SuppressWarnings("deprecation")
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                val intentRequest = activity.intent.getParcelableExtra<AgileRequest>(Butterfly.AGILE_REQUEST)
                if (intentRequest != null) {
                    addEntry(activity, BackStackEntry(intentRequest))
                }
                if (savedInstanceState != null) {
                    val data = savedInstanceState.getParcelableArrayList<AgileRequest>(KEY_SAVE_STATE)
                    if (data != null) {
                        addEntryList(activity, data.map { BackStackEntry(it) })
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                val list = getList(activity)
                if (!list.isNullOrEmpty()) {
                    val savedData = list.mapTo(ArrayList()) { it.request }
                    outState.putParcelableArrayList(KEY_SAVE_STATE, savedData)
                }
            }

            override fun onActivityDestroyed(activity: Activity) = removeList(activity)
        })
    }

    @Synchronized
    private fun removeList(activity: Activity) {
        backStackEntryMap.remove(activity.hashCode())
    }

    @Synchronized
    private fun getList(activity: Activity): List<BackStackEntry>? {
        return backStackEntryMap[activity.hashCode()]
    }

    @Synchronized
    private fun getEntryList(activity: Activity): MutableList<BackStackEntry> {
        var backStackList = backStackEntryMap[activity.hashCode()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            backStackEntryMap[activity.hashCode()] = backStackList
        }

        return backStackList
    }

    @Synchronized
    private fun addEntryList(activity: Activity, entryList: List<BackStackEntry>) {
        getEntryList(activity).addAll(entryList)
    }

    @Synchronized
    fun addEntry(activity: Activity, entry: BackStackEntry) {
        if (isDialogEntry(entry)) {
            getEntryList(activity).add(entry)
        } else {
            val list = getEntryList(activity)
            val dialogEntry = list.firstOrNull { isDialogEntry(it) }
            if (dialogEntry != null) {
                val index = list.indexOf(dialogEntry)
                list.add(index, entry)
            } else {
                list.add(entry)
            }
        }
    }

    @Synchronized
    fun removeTopEntry(activity: Activity): BackStackEntry? {
        return getEntryList(activity).removeLastOrNull()
    }

    @Synchronized
    fun getTopEntry(activity: Activity): BackStackEntry? {
        val entryList = getEntryList(activity)

        return if (entryList.isEmpty()) {
            null
        } else {
            return entryList.lastOrNull()
        }
    }

    @Synchronized
    fun removeEntries(activity: FragmentActivity, entryList: List<BackStackEntry>) {
        getEntryList(activity).removeAll(entryList)
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
    fun findEntry(activity: Activity, block: (BackStackEntry) -> Boolean): BackStackEntry? {
        return getEntryList(activity).lastOrNull(block)
    }

    private fun isDialogEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return DialogFragment::class.java.isAssignableFrom(cls)
    }
}