@file:OptIn(ExperimentalCoroutinesApi::class)

package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResume
import zlc.season.butterfly.ButterflyHelper.remove

class ButterflyFragment : Fragment() {
    companion object {
        fun showAsFlow(
            activity: FragmentActivity,
            intent: Intent,
            options: ActivityOptionsCompat?
        ): Flow<Result<Bundle>> {
            val fragment = ButterflyFragment()
            return activity.awaitFragmentResume(fragment) {
                fragment.viewModel.callback = {
                    trySend(Result.success(it))
                    close()
                }
                fragment.launcher.launch(intent, options)
            }
        }
    }

    private val viewModel by lazy { ViewModelProvider(this)[ButterflyViewModel::class.java] }
    private val launcher = registerForActivityResult(StartActivityForResult()) {
        val result = if (it.resultCode == Activity.RESULT_OK) {
            it.data?.extras ?: Bundle()
        } else {
            Bundle()
        }
        viewModel.callback.invoke(result)

        //clear current fragment
        activity?.remove(this)
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Bundle) -> Unit) = {}
    }
}
