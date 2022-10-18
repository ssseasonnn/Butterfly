package zlc.season.butterfly.dispatcher

import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.logw

object NoneDispatcher : InnerDispatcher<Unit> {
    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        "Agile --> type error".logw()
        return flowOf(Result.failure(IllegalStateException("Agile type error")))
    }

    override fun retreat(target: Unit?, bundle: Bundle): Boolean {
        return if (DialogFragmentDispatcher.retreatCount() > 0) {
            DialogFragmentDispatcher.retreat(null, bundle)
        } else if (FragmentDispatcher.retreatCount() > 0) {
            FragmentDispatcher.retreat(null, bundle)
        } else if (ActivityDispatcher.retreatCount() > 0) {
            ActivityDispatcher.retreat(null, bundle)
        } else {
            false
        }
    }
}