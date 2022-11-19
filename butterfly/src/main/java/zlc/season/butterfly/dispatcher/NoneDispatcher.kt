package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.logw

object NoneDispatcher : InnerDispatcher {
    override suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        "Agile --> type error".logw()
        return flowOf(Result.failure(IllegalStateException("Agile type error")))
    }
}