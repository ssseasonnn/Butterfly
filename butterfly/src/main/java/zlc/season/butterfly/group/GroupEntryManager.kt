package zlc.season.butterfly.group

import android.app.Activity
import android.os.Bundle
import zlc.season.butterfly.AgileRequest
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class GroupEntryManager {
    private val groupData = mutableMapOf<Int, MutableMap<String, MutableList<GroupEntry>>>()

    companion object {
        private const val KEY_SAVE_STATE = "butterfly_group_state"
    }

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (savedInstanceState != null) {
                    val data = savedInstanceState.getParcelableArrayList<AgileRequest>(KEY_SAVE_STATE)
                    if (data != null) {
                        synchronized(this@GroupEntryManager) {
                            val result = data.map { GroupEntry(it) }
                            result.forEach {
                                addEntity(activity, it)
                            }
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                synchronized(this@GroupEntryManager) {
                    val groupMap = groupData[activity.hashCode()]
                    if (!groupMap.isNullOrEmpty()) {
                        val result = ArrayList<AgileRequest>()
                        groupMap.values.forEach {
                            result.addAll(it.map { it.request })
                        }
                        outState.putParcelableArrayList(KEY_SAVE_STATE, result)
                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity) {
                synchronized(this@GroupEntryManager) {
                    groupData.remove(activity.hashCode())
                }
            }
        })
    }

    @Synchronized
    fun addEntity(activity: Activity, groupEntry: GroupEntry) {
        val groupList = getGroupList(activity, groupEntry.request.groupId)
        groupList.add(groupEntry)
    }

    @Synchronized
    fun getGroupList(activity: Activity, groupId: String): MutableList<GroupEntry> {

        var groupMap = groupData[activity.hashCode()]
        if (groupMap == null) {
            groupMap = mutableMapOf()
            groupData[activity.hashCode()] = groupMap
        }

        var groupList = groupMap[groupId]
        if (groupList == null) {
            groupList = mutableListOf()
            groupMap[groupId] = groupList
        }

        return groupList
    }
}