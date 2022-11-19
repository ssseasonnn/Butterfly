package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper.createFragment
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.backstack.BackStackEntryManager

class StandardLauncher : FragmentModeLauncher {
    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }
        return show(request)
    }

    private fun FragmentActivity.show(request: AgileRequest): Fragment {
        val fragment = createFragment(request)
        with(supportFragmentManager.beginTransaction()) {
            setCustomAnimations(request.enterAnim, request.exitAnim, 0, 0)
            if (request.useReplace) {
                replace(request.containerId(), fragment, request.uniqueId)
            } else {
                add(request.containerId(), fragment, request.uniqueId)
            }
            commitAllowingStateLoss()
        }
        return fragment
    }

    private fun AgileRequest.containerId(): Int {
        return if (containerViewId != 0) containerViewId else android.R.id.content
    }
}