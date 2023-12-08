package zlc.season.butterfly.navigator

import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.action.Action
import zlc.season.butterfly.entities.DestinationData

object ActionNavigator : Navigator {
    override suspend fun navigate(context: Context, data: DestinationData): Result<Bundle> {
        return handleAction(context, data)
    }

    private fun handleAction(context: Context, request: DestinationData): Result<Bundle> {
        val action = createAction(request)
        action.doAction(context, request.route, request.bundle)
        return Result.success(Bundle.EMPTY)
    }

    private fun createAction(request: DestinationData): Action {
        val cls = Class.forName(request.className)
        return cls.getDeclaredConstructor().newInstance() as Action
    }
}