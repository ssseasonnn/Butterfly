package zlc.season.butterfly.interceptor

import android.content.Context
import zlc.season.butterfly.entities.DestinationData

interface Interceptor {
    suspend fun shouldIntercept(context: Context, destinationData: DestinationData): Boolean
    suspend fun intercept(context: Context, destinationData: DestinationData): DestinationData
}