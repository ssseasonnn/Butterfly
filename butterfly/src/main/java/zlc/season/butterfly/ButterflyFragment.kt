package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive

class ButterflyFragment : Fragment() {
    companion object {
        private const val TAG = "zlc.season.butterfly.ButterflyFragment"

        private fun FragmentManager.add(fragment: Fragment) {
            beginTransaction().add(fragment, TAG).commitAllowingStateLoss()
        }

        private fun FragmentManager.remove(fragment: Fragment) {
            beginTransaction().remove(fragment).commitAllowingStateLoss()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun showAsFlow(fm: FragmentManager, intent: Intent) = callbackFlow {
            val fragment = ButterflyFragment()
            val cb = object : FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    if (fragment === f) {
                        fragment.viewModel.callback = {
                            trySend(it)
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
                fm.remove(fragment)
                fm.unregisterFragmentLifecycleCallbacks(cb)
            }
        }
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ButterflyViewModel::class.java) }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            Result(it.data)
        } else {
            Result(null)
        }
        viewModel.callback.invoke(result)
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Result) -> Unit) = {}
    }
}
