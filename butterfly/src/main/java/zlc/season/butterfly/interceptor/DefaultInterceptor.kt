package zlc.season.butterfly.interceptor

import android.content.Context
import zlc.season.butterfly.entities.DestinationData

class DefaultInterceptor(
    private val interceptor: suspend (Context, DestinationData) -> DestinationData
) : Interceptor {
    override suspend fun shouldIntercept(
        context: Context,
        destinationData: DestinationData
    ): Boolean {
        return true
    }

    override suspend fun intercept(
        context: Context,
        destinationData: DestinationData
    ): DestinationData {
        return interceptor(context, destinationData)
    }
}