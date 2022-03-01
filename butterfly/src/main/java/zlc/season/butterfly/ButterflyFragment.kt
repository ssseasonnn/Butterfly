package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class ButterflyFragment : Fragment() {
    companion object {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun showAsFlow(fm: FragmentManager, intent: Intent): Flow<Result<Intent>> {
            val fragment = ButterflyFragment()
            return fm.showAsFlow(fragment) {
                fragment.viewModel.callback = {
                    trySend(Result.success(it))
                    close()
                }
                fragment.launcher.launch(intent)
            }
        }
    }

    private val viewModel by lazy { ViewModelProvider(this).get(ButterflyViewModel::class.java) }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            it.data ?: Intent()
        } else {
            Intent()
        }
        viewModel.callback.invoke(result)

        //clear current fragment
        activity?.let { p ->
            p.supportFragmentManager.remove(this)
        }
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Intent) -> Unit) = {}
    }
}
