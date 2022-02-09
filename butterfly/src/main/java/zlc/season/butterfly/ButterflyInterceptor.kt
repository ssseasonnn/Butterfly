package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import zlc.season.butterfly.Butterfly.logd

class ButterflyInterceptor : Interceptor {
    override suspend fun intercept(context: Context, request: Request): Request {
        val type = getType(request)
        "ButterflyInterceptor intercepted! Type -> $type".logd()
        return request.copy(type = type)
    }

    private fun getType(request: Request): String {
        val dest = request.target
        val cls = Class.forName(dest)
        return when {
            Service::class.java.isAssignableFrom(cls) -> Request.TYPE_SERVICE
            DialogFragment::class.java.isAssignableFrom(cls) -> Request.TYPE_DIALOG_FRAGMENT
            Fragment::class.java.isAssignableFrom(cls) -> Request.TYPE_FRAGMENT
            Activity::class.java.isAssignableFrom(cls) -> Request.TYPE_ACTIVITY
            else -> Request.TYPE_ACTIVITY
        }
    }
}