package zlc.season.butterfly.launcher

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import zlc.season.butterfly.ButterflyCore
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.core.InterceptorManager

class DestinationLauncher(
    val context: Context,
    val destinationData: DestinationData,
    val interceptorManager: InterceptorManager
) {
    val flow = MutableSharedFlow<Result<Bundle>>(extraBufferCapacity = 1)

    fun result(): Flow<Result<Bundle>> {
        return flow
    }

    suspend fun launch() {
        ButterflyCore.dispatchNavigate(context, destinationData, interceptorManager)
    }
}