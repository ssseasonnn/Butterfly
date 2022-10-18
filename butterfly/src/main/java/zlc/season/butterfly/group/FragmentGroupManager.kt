package zlc.season.butterfly.group

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion.application

class FragmentGroupManager {
    private val groupData = mutableMapOf<Int, MutableMap<String, MutableList<FragmentGroupEntity>>>()

    companion object {
        private const val KEY_SAVE_STATE = "butterfly_group_state"
    }

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (savedInstanceState != null) {
                    val data = savedInstanceState.getParcelableArrayList<AgileRequest>(KEY_SAVE_STATE)
                    if (data != null) {
                        synchronized(this@FragmentGroupManager) {
                            val result = data.map { FragmentGroupEntity(it) }
                            result.forEach {
                                addEntity(activity, it)
                            }
                        }
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                synchronized(this@FragmentGroupManager) {
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
                synchronized(this@FragmentGroupManager) {
                    groupData.remove(activity.hashCode())
                }
            }
        })
    }

    @Synchronized
    fun addEntity(activity: Activity, groupEntity: FragmentGroupEntity) {
        val groupList = getGroupList(activity, groupEntity.request)
        groupList.add(groupEntity)
    }

    @Synchronized
    fun getGroupList(activity: Activity, request: AgileRequest): MutableList<FragmentGroupEntity> {
        val groupName = request.fragmentConfig.groupId

        var groupMap = groupData[activity.hashCode()]
        if (groupMap == null) {
            groupMap = mutableMapOf()
            groupData[activity.hashCode()] = groupMap
        }

        var groupList = groupMap[groupName]
        if (groupList == null) {
            groupList = mutableListOf()
            groupMap[groupName] = groupList
        }

        return groupList
    }
}