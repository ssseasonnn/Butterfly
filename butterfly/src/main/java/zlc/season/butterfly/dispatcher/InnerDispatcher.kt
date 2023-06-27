package zlc.season.butterfly.dispatcher

import android.app.Activity
import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry

interface InnerDispatcher {
    suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> = emptyFlow()

    fun retreat(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {}
}