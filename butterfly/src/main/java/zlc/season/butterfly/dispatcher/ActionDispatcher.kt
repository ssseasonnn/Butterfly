package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.Action
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.ButterflyHelper

object ActionDispatcher : InnerDispatcher {
    override suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val cls = Class.forName(request.className)
        val action = cls.newInstance() as Action
        action.doAction(ButterflyHelper.context, request.scheme, request.bundle)
        return emptyFlow()
    }

    override suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> {
        val cls = Class.forName(request.className)
        val action = cls.newInstance() as Action
        action.doAction(activity, request.scheme, request.bundle)
        return emptyFlow()
    }
}