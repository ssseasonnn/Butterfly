package zlc.season.butterfly.group

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter
import zlc.season.claritypotion.ClarityPotion

class FragmentGroupManager {
    private val groupData = mutableMapOf<Int, MutableMap<String, MutableList<FragmentGroupEntity>>>()

    init {
        ClarityPotion.application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityDestroyed(activity: Activity) {
                synchronized(this@FragmentGroupManager) {
                    groupData.remove(activity.hashCode())
                }
            }
        })
    }

    @Synchronized
    fun addEntity(activity: FragmentActivity, groupEntity: FragmentGroupEntity) {
        val groupList = getGroupList(activity, groupEntity.request)
        groupList.add(groupEntity)
    }

    @Synchronized
    fun getGroupList(activity: FragmentActivity, request: AgileRequest): MutableList<FragmentGroupEntity> {
        val groupName = request.fragmentConfig.groupName

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

        clearUselessEntity(groupList)

        return groupList
    }

    private fun clearUselessEntity(groupList: MutableList<FragmentGroupEntity>) {
        val shouldRemoveList = mutableListOf<FragmentGroupEntity>()
        groupList.forEach {
            if (it.reference.get() == null) {
                shouldRemoveList.add(it)
            }
        }
        groupList.removeAll(shouldRemoveList)
    }
}