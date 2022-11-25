package zlc.season.butterfly.internal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import zlc.season.butterfly.AgileLauncherManager

class ButterflyFragment : Fragment() {
    companion object {
        private const val KEY_SCHEME = "key_scheme"
        private const val KEY_REQUEST_ID = "key_request_id"

        fun FragmentActivity.awaitActivityResult(scheme: String, intent: Intent): Flow<Result<Bundle>> {
            val fragment = ButterflyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SCHEME, scheme)
                }
            }
            return awaitFragmentResume(fragment) {
                fragment.viewModel.callback = {
                    trySend(Result.success(it))
                    close()
                }
                fragment.launcher.launch(intent)
            }
        }

        fun FragmentActivity.awaitFragmentResult(scheme: String, requestId: String): Flow<Result<Bundle>> {
            val fragment = ButterflyFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_SCHEME, scheme)
                    putString(KEY_REQUEST_ID, requestId)
                }
            }
            return awaitFragmentResume(fragment) {
                fragment.viewModel.callback = {
                    trySend(Result.success(it))
                    close()
                }
            }
        }
    }

    private val scheme by lazy { arguments?.getString(KEY_SCHEME) ?: "" }
    private val requestId by lazy { arguments?.getString(KEY_REQUEST_ID) ?: "" }

    private val viewModel by lazy { ViewModelProvider(this)[ButterflyViewModel::class.java] }
    private val launcher = registerForActivityResult(StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            it.data?.extras ?: Bundle()
        } else {
            Bundle()
        }

        //set result for callback
        viewModel.callback.invoke(result)

        //set result for launcher
        val launcher = AgileLauncherManager.getLauncher(requireActivity().key(), scheme)
        launcher?.flow?.tryEmit(Result.success(result))

        //clear current fragment
        activity?.removeFragment(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (requestId.isNotEmpty()) {
            parentFragmentManager.setFragmentResultListener(requestId, this) { requestKey, result ->
                if (requestId == requestKey) {
                    //set result for callback
                    viewModel.callback.invoke(result)

                    //set result for launcher
                    val launcher = AgileLauncherManager.getLauncher(requireActivity().key(), scheme)
                    launcher?.flow?.tryEmit(Result.success(result))

                    parentFragmentManager.clearFragmentResultListener(requestKey)

                    //clear current fragment
                    activity?.removeFragment(this)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.callback = {}

        if (requestId.isNotEmpty()) {
            parentFragmentManager.clearFragmentResultListener(requestId)
        }
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Bundle) -> Unit) = {}
    }
}
