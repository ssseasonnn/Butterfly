package zlc.season.butterfly.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.Action
import zlc.season.butterfly.AgileRequest

object ActionDispatcher : InnerDispatcher {
    override suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        return handleAction(context, request)
    }

    override suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        return handleAction(activity, request)
    }

    private fun handleAction(context: Context, request: AgileRequest): Flow<Result<Bundle>> {
        val action = createAction(request)
        action.doAction(context, request.scheme, request.bundle)
        return emptyFlow()
    }

    private fun createAction(request: AgileRequest): Action {
        val cls = Class.forName(request.className)
        return cls.newInstance() as Action
    }
}