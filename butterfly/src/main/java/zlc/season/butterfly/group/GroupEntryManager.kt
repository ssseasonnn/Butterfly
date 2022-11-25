package zlc.season.butterfly.group

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.internal.observeFragmentDestroy
import zlc.season.butterfly.internal.logd
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

@Suppress("DEPRECATION")
class GroupEntryManager {
    private val groupEntryMap = mutableMapOf<String, MutableList<GroupEntry>>()

    companion object {
        private const val KEY_SAVE_STATE = "butterfly_group_state"
    }

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (savedInstanceState != null) {
                    restoreEntryList(activity, savedInstanceState)
                }
            }

            override fun onActivityPostCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is FragmentActivity) {
                    activity.observeFragmentDestroy {
                        val uniqueId = it.tag
                        if (!uniqueId.isNullOrEmpty()) {
                            removeEntry(activity, uniqueId)
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                saveEntryList(activity, outState)
            }

            override fun onActivityDestroyed(activity: Activity) {
                destroyEntryList(activity)
            }
        })
    }

    @Synchronized
    private fun restoreEntryList(activity: Activity, savedState: Bundle) {
        val data = savedState.getParcelableArrayList<AgileRequest>(KEY_SAVE_STATE)
        if (data != null) {
            val entryList = data.map { GroupEntry(it) }
            getEntryList(activity).addAll(entryList)

            "Group ---> ${activity.key()} restore entry list".logd()
            "Group ---> Result -> $groupEntryMap".logd()
        }
    }

    @Synchronized
    private fun saveEntryList(activity: Activity, outState: Bundle) {
        val entryList = groupEntryMap[activity.key()]
        if (!entryList.isNullOrEmpty()) {
            val savedData = entryList.mapTo(ArrayList()) { it.request }
            outState.putParcelableArrayList(KEY_SAVE_STATE, savedData)

            "Group ---> ${activity.key()} save entry list".logd()
        }
    }

    @Synchronized
    private fun destroyEntryList(activity: Activity) {
        groupEntryMap.remove(activity.key())

        "Group ---> ${activity.key()} destroy entry list".logd()
        "Group ---> Result -> $groupEntryMap".logd()
    }

    @Synchronized
    private fun removeEntry(activity: Activity, uniqueId: String) {
        val entryList = getEntryList(activity)
        val find = entryList.find { it.request.uniqueId == uniqueId }
        if (find != null) {
            entryList.remove(find)

            "Group ---> ${activity.key()} removeEntry -> $find".logd()
            "Group ---> Result -> $groupEntryMap".logd()
        }
    }

    @Synchronized
    fun addEntry(activity: Activity, groupEntry: GroupEntry) {
        val entryList = getEntryList(activity)
        entryList.add(groupEntry)

        "Group ---> ${activity.key()} addEntry -> $groupEntry".logd()
        "Group ---> Result -> $groupEntryMap".logd()
    }

    @Synchronized
    fun getGroupList(activity: Activity, groupId: String): List<GroupEntry> {
        val result = mutableListOf<GroupEntry>()
        val list = getEntryList(activity)
        list.forEach {
            if (it.request.groupId == groupId) {
                result.add(it)
            }
        }
        return result
    }

    @Synchronized
    private fun getEntryList(activity: Activity): MutableList<GroupEntry> {
        var groupList = groupEntryMap[activity.key()]
        if (groupList == null) {
            groupList = mutableListOf()
            groupEntryMap[activity.key()] = groupList
        }
        return groupList
    }

    private fun Activity.key(): String {
        return "Activity@${hashCode()}"
    }
}