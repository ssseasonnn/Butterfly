package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.Action
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.internal.ButterflyHelper

object ActionDispatcher : InnerDispatcher {
    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val action = createAction(request)
        action.doAction(ButterflyHelper.context, request.scheme, request.bundle)
        return emptyFlow()
    }

    override suspend fun dispatchByActivity(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        val action = createAction(request)
        action.doAction(activity, request.scheme, request.bundle)
        return emptyFlow()
    }

    private fun createAction(request: AgileRequest): Action {
        val cls = Class.forName(request.className)
        return cls.newInstance() as Action
    }
}