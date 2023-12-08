package zlc.season.butterfly.navigator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData

interface Navigator {
    suspend fun navigate(context: Context, data: DestinationData): Result<Bundle>

    fun popBack(activity: Activity, topEntry: BackStackEntry, bundle: Bundle) {}
}