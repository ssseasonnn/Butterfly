package zlc.season.butterfly.internal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import zlc.season.butterfly.launcher.DestinationLauncherManager
import zlc.season.butterfly.navigator.fragment.addFragment
import zlc.season.butterfly.navigator.fragment.awaitFragmentResume
import zlc.season.butterfly.navigator.fragment.removeFragment

class ButterflyFragment : Fragment() {
    companion object {
        private const val KEY_ROUTE = "key_destination_route"
        private const val KEY_TAG = "key_destination_tag"

        suspend fun FragmentActivity.startActivityAndAwaitResult(
            route: String,
            intent: Intent
        ): Result<Bundle> {
            "Await activity result: currentActivity=$this, route=$route".logd()
            val fragment = ButterflyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ROUTE, route)
                }
            }

            "Await activity result: add fragment $fragment...".logd()
            addFragment(fragment)

            "Await activity result: waiting fragment resume...".logd()
            awaitFragmentResume(fragment)

            "Await activity result: waiting activity result...".logd()
            val result = fragment.startActivityAndAwaitResult(intent)

            "Await activity result: result=$result.".logd()
            return Result.success(result)
        }

        suspend fun FragmentActivity.awaitFragmentResult(
            route: String,
            uniqueTag: String
        ): Result<Bundle> {
            "Await fragment result: currentActivity=$this, route=$route, uniqueTag=$uniqueTag".logd()
            val fragment = ButterflyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_ROUTE, route)
                    putString(KEY_TAG, uniqueTag)
                }
            }

            "Await fragment result: add fragment $fragment...".logd()
            addFragment(fragment)

            "Await fragment result: waiting fragment resume...".logd()
            awaitFragmentResume(fragment)

            "Await fragment result: waiting fragment result...".logd()
            val result = fragment.waitFragmentResult()

            "Await fragment result: result=$result.".logd()
            return Result.success(result)
        }
    }

    private val route by lazy { arguments?.getString(KEY_ROUTE) ?: "" }
    private val uniqueTag by lazy { arguments?.getString(KEY_TAG) ?: "" }

    private val viewModel by lazy { ViewModelProvider(this)[ButterflyViewModel::class.java] }
    private val launcher = registerForActivityResult(StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            it.data?.extras ?: Bundle()
        } else {
            Bundle()
        }

        "ButterflyFragment[$this] onActivityResult: result=$result".logd()
        //set result for callback
        viewModel.callback.invoke(result)

        //set result for launcher
        val launcher = DestinationLauncherManager.getLauncher(requireActivity().key(), route)
        launcher?.flow?.tryEmit(Result.success(result))

        //clear current fragment
        activity?.removeFragment(this)
    }

    /**
     * Launch activity and wait result.
     */
    suspend fun startActivityAndAwaitResult(intent: Intent): Bundle = suspendCancellableCoroutine {
        viewModel.callback = { result ->
            it.resumeWith(Result.success(result))
            viewModel.callback = {}
        }

        "ButterflyFragment[$this] launch activity and wait result".logd()
        launcher.launch(intent)

        it.invokeOnCancellation {
            viewModel.callback = {}
        }
    }

    suspend fun waitFragmentResult(): Bundle = suspendCancellableCoroutine {
        viewModel.callback = { result ->
            it.resumeWith(Result.success(result))
            viewModel.callback = {}
        }

        "ButterflyFragment[$this] wait fragment result".logd()

        it.invokeOnCancellation {
            viewModel.callback = {}
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        "ButterflyFragment[$this] onCreate".logd()

        if (uniqueTag.isNotEmpty()) {
            parentFragmentManager.setFragmentResultListener(uniqueTag, this) { requestKey, result ->
                if (uniqueTag == requestKey) {
                    "ButterflyFragment[$this] onFragmentResult: result=$result".logd()

                    //set result for callback
                    viewModel.callback.invoke(result)

                    //set result for launcher
                    val launcher =
                        DestinationLauncherManager.getLauncher(requireActivity().key(), route)
                    launcher?.flow?.tryEmit(Result.success(result))

                    parentFragmentManager.clearFragmentResultListener(requestKey)

                    //clear current fragment
                    activity?.removeFragment(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        "ButterflyFragment[$this] onResume".logd()
    }

    override fun onDestroy() {
        super.onDestroy()
        "ButterflyFragment[$this] onDestroy".logd()

        viewModel.callback = {}

        if (uniqueTag.isNotEmpty()) {
            parentFragmentManager.clearFragmentResultListener(uniqueTag)
        }
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Bundle) -> Unit) = {}
    }
}
