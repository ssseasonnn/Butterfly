package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ButterflyFragment : Fragment() {
    companion object {
        fun show(fm: FragmentManager, intent: Intent, onResult: (Intent) -> Unit) {
            val fragment = ButterflyFragment()
            fm.show(fragment) {
                it.viewModel.callback = onResult
                it.launcher.launch(intent)
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
    }

    class ButterflyViewModel : ViewModel() {
        var callback: ((Intent) -> Unit) = {}
    }
}
