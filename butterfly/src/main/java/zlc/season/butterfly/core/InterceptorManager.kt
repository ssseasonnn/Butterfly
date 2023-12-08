package zlc.season.butterfly.core

import android.content.Context
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.interceptor.Interceptor

class InterceptorManager {
    private val interceptorList = mutableListOf<Interceptor>()

    fun addInterceptor(interceptor: Interceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: Interceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(context: Context, destinationData: DestinationData): DestinationData {
        val temp = mutableListOf<Interceptor>()
        temp.addAll(interceptorList)

        var tempData = destinationData
        temp.forEach {
            if (it.shouldIntercept(context, tempData)) {
                tempData = it.intercept(context, tempData)
            }
        }

        return tempData
    }
}