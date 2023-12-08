package zlc.season.butterfly.interceptor

import zlc.season.butterfly.entities.DestinationData

interface Interceptor {
    suspend fun shouldIntercept(destinationData: DestinationData): Boolean
    suspend fun intercept(destinationData: DestinationData): DestinationData
}