package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import zlc.season.butterfly.internal.ButterflyHelper.findScope

class AgileLauncher(
    val context: Context,
    val agileRequest: AgileRequest,
    val interceptorManager: InterceptorManager
) {
    val flow = MutableSharedFlow<Result<Bundle>>(extraBufferCapacity = 1)

    fun result(): Flow<Result<Bundle>> {
        return flow
    }

    fun launch() {
        ButterflyCore.dispatchAgile(context, agileRequest, interceptorManager).launchIn(context.findScope())
    }
}