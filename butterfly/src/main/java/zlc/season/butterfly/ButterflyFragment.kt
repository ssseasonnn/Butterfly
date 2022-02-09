package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

class ButterflyFragment : Fragment() {
    companion object {
        private const val TAG = "zlc.season.butterfly.ButterflyFragment"

        private fun FragmentManager.add(fragment: Fragment) {
            beginTransaction().add(fragment, TAG).commitNowAllowingStateLoss()
        }

        private fun FragmentManager.remove(fragment: Fragment) {
            beginTransaction().remove(fragment).commitNowAllowingStateLoss()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun showAsFlow(fm: FragmentManager, intent: Intent) = callbackFlow {
            val fragment = ButterflyFragment()
            val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        fragment.callback = {
                            trySend(it)
                            fm.remove(fragment)
                            close()
                        }
                        fragment.launcher.launch(intent)
                    }
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        close()
                    }
                }
            }

            if (isActive) {
                fm.registerFragmentLifecycleCallbacks(cb, false)
                fm.add(fragment)
            }

            awaitClose {
                fm.unregisterFragmentLifecycleCallbacks(cb)
            }
        }
    }

    init {
        retainInstance = true
    }

    var callback: (Result) -> Unit = {}

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            Result(it.data)
        } else {
            Result(null)
        }
        callback(result)
    }
}
