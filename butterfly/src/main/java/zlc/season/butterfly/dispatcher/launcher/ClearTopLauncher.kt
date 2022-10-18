package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.remove
import zlc.season.butterfly.backstack.BackStackEntryManager

class ClearTopLauncher : NonStandardLauncher() {
    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(backStackEntryManager, request)
        } else {
            val targetEntry = topEntryList.removeFirst()
            topEntryList.forEach {
                remove(it.request.uniqueId)
            }
            backStackEntryManager.removeEntries(this, topEntryList)
            tryLaunch(backStackEntryManager, targetEntry.request, request)
        }
    }
}