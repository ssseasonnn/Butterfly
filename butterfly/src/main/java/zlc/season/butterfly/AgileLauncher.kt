package zlc.season.butterfly

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.logw

class AgileLauncher(
    val agileRequest: AgileRequest,
    val interceptorManager: InterceptorManager
) {
    val flow = MutableSharedFlow<Result<Bundle>>(extraBufferCapacity = 1)

    fun result(): Flow<Result<Bundle>> {
        return flow
    }

    fun launch() {
        val activity = ButterflyHelper.fragmentActivity
        if (activity == null) {
            "No valid Activity found!".logw()
            return
        }
        ButterflyCore.dispatchAgile(agileRequest, interceptorManager).launchIn(activity.lifecycleScope)
    }
}