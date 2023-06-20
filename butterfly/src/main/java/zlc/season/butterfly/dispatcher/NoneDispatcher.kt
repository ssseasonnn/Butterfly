package zlc.season.butterfly.dispatcher

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.internal.logw

object NoneDispatcher : InnerDispatcher {
    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        return error()
    }

    private fun error(): Flow<Result<Bundle>> {
        "Agile --> type error".logw()
        return flowOf(Result.failure(IllegalStateException("Agile type error")))
    }
}