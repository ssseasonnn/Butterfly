package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.internal.findFragment
import zlc.season.butterfly.internal.showFragment

abstract class NonStandardLauncher : FragmentModeLauncher {
    private val standardLauncher = StandardLauncher()

    protected fun FragmentActivity.standardLaunch(backStackEntryManager: BackStackEntryManager, request: AgileRequest): Fragment {
        return with(standardLauncher) {
            launch(backStackEntryManager, request)
        }
    }

    protected fun FragmentActivity.tryLaunch(
        backStackEntryManager: BackStackEntryManager,
        oldRequest: AgileRequest,
        newRequest: AgileRequest
    ): Fragment {
        val target = findFragment(oldRequest)
        return if (target == null) {
            standardLaunch(backStackEntryManager, newRequest)
        } else {
            if (target is OnFragmentNewArgument) {
                target.onNewArgument(newRequest.bundle)
            }
            showFragment(target)
            target
        }
    }
}