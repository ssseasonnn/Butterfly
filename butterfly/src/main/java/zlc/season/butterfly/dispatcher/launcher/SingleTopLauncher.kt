package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager

class SingleTopLauncher : NonStandardLauncher() {
    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.request.className == request.className) {
            tryLaunch(backStackEntryManager, topEntry.request, request)
        } else {
            standardLaunch(backStackEntryManager, request)
        }
    }
}