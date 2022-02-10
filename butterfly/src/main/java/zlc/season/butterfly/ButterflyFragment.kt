package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
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

        private fun getFragment(fm: FragmentManager): ButterflyFragment {
            return fm.findFragmentByTag(TAG) as? ButterflyFragment ?: ButterflyFragment()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        fun showAsFlow(fm: FragmentManager, intent: Intent) = callbackFlow {
            val fragment = getFragment(fm)
            if (fragment.lifecycle.currentState == Lifecycle.State.RESUMED) {
                fragment.callback = {
                    trySend(it)
                    close()
                }
                if (isActive) {
                    fragment.launcher.launch(intent)
                }
                awaitClose {
                    println("await close")
                }
            } else {
                val cb = object : FragmentManager.FragmentLifecycleCallbacks() {
                    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                        if (fragment === f) {
                            fragment.callback = {
                                trySend(it)
                                close()
                            }
                            fragment.launcher.launch(intent)
                            println("on fragment resumed")
                        }
                    }

                    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                        if (fragment === f) {
                            close()
                            println("on fragment destroyed")
                        }
                    }
                }

                if (isActive) {
                    fm.registerFragmentLifecycleCallbacks(cb, false)
                    fm.add(fragment)
                }
                awaitClose {
                    fm.unregisterFragmentLifecycleCallbacks(cb)
                    println("await close")
                }
            }
        }
    }

    var callback: (Result) -> Unit = {}

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        println("on result")
        val result = if (it.resultCode == Activity.RESULT_OK) {
            Result(it.data)
        } else {
            Result(null)
        }
        callback(result)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("oncreate")
    }

    override fun onStart() {
        super.onStart()
        println("on start")
    }

    override fun onResume() {
        super.onResume()
        println("on resume")
    }

    override fun onPause() {
        super.onPause()
        println("on pause")
    }

    override fun onStop() {
        super.onStop()
        println("on stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("on destroy")
    }
}
