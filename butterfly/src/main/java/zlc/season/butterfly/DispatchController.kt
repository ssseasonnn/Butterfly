package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class DispatchController {
    companion object {
        const val SCHEME = "butterfly_scheme"
    }

    fun dispatch(context: Context, request: Request) {
        val intent = createIntent(context, request)
        context.startActivity(intent)
    }

    suspend fun dispatchWithResult(context: Context, request: Request): Result {
        val intent = createIntent(context, request)
        if (context is FragmentActivity) {
            val fm = context.supportFragmentManager
            return ButterflyFragment.showAsFlow(fm, intent)
                .onStart { }
                .onCompletion { }
                .first()
        } else {
            return Result()
        }
    }

    private fun createIntent(context: Context, request: Request): Intent {
        val intent = Intent()
        intent.putExtra(SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.dest)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
}