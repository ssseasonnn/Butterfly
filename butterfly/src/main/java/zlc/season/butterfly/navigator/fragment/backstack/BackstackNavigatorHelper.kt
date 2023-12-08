package zlc.season.butterfly.navigator.fragment.backstack

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.navigator.fragment.findFragment
import zlc.season.butterfly.navigator.fragment.showFragment
import zlc.season.butterfly.navigator.fragment.OnFragmentNewArgument
import zlc.season.butterfly.navigator.fragment.awaitFragmentResume
import zlc.season.butterfly.navigator.fragment.createAndShowFragment

class BackstackNavigatorHelper {

    suspend fun createAndShowFragment(
        activity: FragmentActivity,
        destinationData: DestinationData
    ): Fragment {
        val fragment = activity.createAndShowFragment(destinationData)
        activity.awaitFragmentResume(fragment)
        return fragment
    }

    suspend fun showFragmentAndUpdateArguments(
        activity: FragmentActivity,
        oldData: DestinationData,
        newData: DestinationData
    ): Fragment {
        val target = activity.findFragment(oldData)
        return if (target == null) {
            createAndShowFragment(activity, newData)
        } else {
            if (target is OnFragmentNewArgument) {
                target.onNewArgument(newData.bundle)
            }
            activity.showFragment(target)
            activity.awaitFragmentResume(target)
            target
        }
    }
}