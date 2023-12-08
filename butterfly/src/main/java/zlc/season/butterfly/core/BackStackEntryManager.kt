package zlc.season.butterfly.core

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.ButterflyHelper.KEY_DESTINATION_DATA
import zlc.season.butterfly.internal.ButterflyHelper.getDestinationData
import zlc.season.butterfly.internal.key
import zlc.season.butterfly.navigator.fragment.observeFragmentDestroy
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter

class BackStackEntryManager {
    companion object {
        private const val KEY_SAVE_STATE = "butterfly_back_stack_state"
    }

    /**
     * Activity as the key to save the BackStackEntry corresponding to each activity.
     *
     * like:
     * {
     *   {ActivityA} -> [BackStackEntryA1, BackStackEntryA2],
     *   {ActivityB} -> [BackStackEntryB1, BackStackEntryB2]
     * }
     */
    private val backStackEntryMap = mutableMapOf<String, MutableList<BackStackEntry>>()

    init {
        ButterflyHelper.application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                // If activity is recreate, then restore back stack entry list.
                if (savedInstanceState != null) {
                    restoreEntryList(activity, savedInstanceState)
                } else {
                    // If activity is started by Butterfly, then add it into back stack list.
                    val data = activity.getDestinationData()
                    if (data != null) {
                        addEntry(activity, BackStackEntry(data))
                    }
                }

                // 监听Fragment销毁事件，移除Fragment Entry
                if (activity is FragmentActivity) {
                    activity.observeFragmentDestroy {
                        val uniqueTag = it.tag
                        if (!uniqueTag.isNullOrEmpty()) {
                            removeEntry(activity, uniqueTag)
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) =
                saveEntryList(activity, outState)

            override fun onActivityDestroyed(activity: Activity) = destroyEntryList(activity)
        })
    }

    @Synchronized
    private fun restoreEntryList(activity: Activity, savedState: Bundle) {
        val data = savedState.getParcelableArrayList<DestinationData>(KEY_SAVE_STATE)
        if (data != null) {
            val entryList = data.map { BackStackEntry(it) }
            getEntryList(activity).addAll(entryList)
        }
    }

    @Synchronized
    private fun saveEntryList(activity: Activity, outState: Bundle) {
        val list = backStackEntryMap[activity.key()]
        if (!list.isNullOrEmpty()) {
            val savedData = list.mapTo(ArrayList()) { it.destinationData }
            outState.putParcelableArrayList(KEY_SAVE_STATE, savedData)
        }
    }

    @Synchronized
    private fun destroyEntryList(activity: Activity) {
        backStackEntryMap.remove(activity.key())
    }

    @Synchronized
    private fun removeEntry(activity: Activity, uniqueTag: String) {
        val find = getEntryList(activity).find { it.destinationData.uniqueTag == uniqueTag }
        if (find != null) {
            getEntryList(activity).remove(find)
        }
    }

    @Synchronized
    fun removeTopEntry(activity: Activity): BackStackEntry? {
        return getEntryList(activity).removeLastOrNull()
    }

    @Synchronized
    fun removeEntryList(activity: Activity, entryList: List<BackStackEntry>) {
        getEntryList(activity).removeAll(entryList)
    }

    @Synchronized
    fun addEntry(activity: Activity, entry: BackStackEntry) {
        val list = getEntryList(activity)

        if (isDialogEntry(entry)) {
            list.add(entry)
        } else {
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
    fun getTopEntry(activity: Activity): BackStackEntry? {
        val entryList = getEntryList(activity)

        return if (entryList.isEmpty()) {
            null
        } else {
            return entryList.lastOrNull()
        }
    }

    @Synchronized
    fun getTopEntryList(activity: Activity, data: DestinationData): MutableList<BackStackEntry> {
        val result = mutableListOf<BackStackEntry>()

        val backStackList = getEntryList(activity)
        val index = backStackList.indexOfLast { it.destinationData.className == data.className }
        if (index != -1) {
            for (i in index until backStackList.size) {
                val entry = backStackList[i]
                result.add(entry)
            }
        }
        return result
    }

    @Synchronized
    fun getEntryList(activity: Activity): MutableList<BackStackEntry> {
        var backStackList = backStackEntryMap[activity.key()]
        if (backStackList == null) {
            backStackList = mutableListOf()
            backStackEntryMap[activity.key()] = backStackList
        }

        return backStackList
    }

    private fun isDialogEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.destinationData.className)
        return DialogFragment::class.java.isAssignableFrom(cls)
    }
}