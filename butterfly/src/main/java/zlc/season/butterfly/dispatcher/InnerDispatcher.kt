package zlc.season.butterfly.dispatcher

import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import zlc.season.butterfly.AgileRequest

interface InnerDispatcher {
    suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>>

    fun retreat(request: AgileRequest, bundle: Bundle) {}

    fun retreat(bundle: Bundle) {}
}