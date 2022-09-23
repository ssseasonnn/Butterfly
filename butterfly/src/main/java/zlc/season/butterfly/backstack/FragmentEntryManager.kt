package zlc.season.butterfly.backstack

import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest

class FragmentEntryManager : BaseEntryManager<FragmentEntry>() {
    @Synchronized
    fun removeEntries(activity: FragmentActivity, entryList: List<FragmentEntry>) {
        val backStackList = getEntryList(activity)
        backStackList.removeAll(entryList)
    }

    @Synchronized
    fun getTopEntryList(activity: FragmentActivity, request: AgileRequest): MutableList<FragmentEntry> {
        val result = mutableListOf<FragmentEntry>()

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
}
