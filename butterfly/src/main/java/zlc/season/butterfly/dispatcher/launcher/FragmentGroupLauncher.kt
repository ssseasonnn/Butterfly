package zlc.season.butterfly.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.group.FragmentGroupManager

interface FragmentGroupLauncher {
    fun FragmentActivity.launch(fragmentGroupManager: FragmentGroupManager, request: AgileRequest): Fragment
}