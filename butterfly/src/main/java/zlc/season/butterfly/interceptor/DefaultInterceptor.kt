package zlc.season.butterfly.interceptor

import zlc.season.butterfly.entities.DestinationData

class DefaultInterceptor(
    private val interceptor: suspend (DestinationData) -> DestinationData
) : Interceptor {
    override suspend fun shouldIntercept(destinationData: DestinationData): Boolean {
        return true
    }

    override suspend fun intercept(destinationData: DestinationData): DestinationData {
        return interceptor(destinationData)
    }
}