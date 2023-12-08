package zlc.season.butterfly.navigator

import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.entities.DestinationData

object ErrorNavigator : Navigator {
    override suspend fun navigate(
        context: Context,
        data: DestinationData
    ): Result<Bundle> {
        return Result.failure(IllegalStateException("Invalid destination data: $data"))
    }
}