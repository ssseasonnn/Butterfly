package zlc.season.butterfly

class InterceptorManager {
    private val interceptorList = mutableListOf<ButterflyInterceptor>()

    fun addInterceptor(interceptor: ButterflyInterceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: ButterflyInterceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(agileRequest: AgileRequest): AgileRequest {
        val temp = mutableListOf<ButterflyInterceptor>()
        temp.addAll(interceptorList)

        var tempRequest = agileRequest
        temp.forEach {
            if (it.shouldIntercept(tempRequest)) {
                tempRequest = it.intercept(tempRequest)
            }
        }

        return tempRequest
    }
}