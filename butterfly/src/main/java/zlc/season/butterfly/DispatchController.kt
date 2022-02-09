package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import zlc.season.butterfly.Butterfly.logd

class DispatchController {
    companion object {
        const val SCHEME = "butterfly_scheme"
    }

    suspend fun dispatch(context: Context, request: Request) {
        if (request.type == Request.TYPE_ACTIVITY) {
            val intent = createIntent(context, request)
            context.startActivity(intent)
            "dispatch activity".logd()
        } else if (request.type == Request.TYPE_SERVICE) {
            val cls = Class.forName(request.target)
            val service = cls.newInstance() as Service
            service.start(context, request)
            "dispatch service".logd()
        }
    }

    suspend fun dispatchWithResult(context: Context, request: Request): Result {
        val intent = createIntent(context, request)
        return if (context is FragmentActivity) {
            val fm = context.supportFragmentManager
            ButterflyFragment.showAsFlow(fm, intent)
                .onStart { }
                .onCompletion { }
                .first()
        } else {
            Result()
        }
    }

    private fun createIntent(context: Context, request: Request): Intent {
        val intent = Intent()
        intent.putExtra(SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.target)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
}