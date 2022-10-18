package zlc.season.butterfly.dispatcher

import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import zlc.season.butterfly.AgileRequest

interface InnerDispatcher<T> {
    suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>>

    fun retreat(target: T?, bundle: Bundle): Boolean {
        return false
    }

    fun retreatCount(): Int {
        return 0
    }
}