package zlc.season.butterfly

import android.content.Context

class InterceptorController {
    private val interceptorList = mutableListOf<Interceptor>()

    fun addInterceptor(interceptor: Interceptor) {
        interceptorList.add(interceptor)
    }

    suspend fun intercept(context: Context, request: Request): Request {
        var newRequest = request
        interceptorList.forEach {
            newRequest = it.intercept(context, newRequest)
        }
        return newRequest
    }
}