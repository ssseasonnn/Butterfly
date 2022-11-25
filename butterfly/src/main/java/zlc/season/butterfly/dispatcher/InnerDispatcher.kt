package zlc.season.butterfly.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry

interface InnerDispatcher {
    suspend fun dispatch(context: Context, request: AgileRequest): Flow<Result<Bundle>> = emptyFlow()

    suspend fun dispatch(activity: FragmentActivity, request: AgileRequest): Flow<Result<Bundle>> = emptyFlow()

    fun retreat(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {}

    fun onRetreat(activity: FragmentActivity, topEntry: BackStackEntry) {}
}