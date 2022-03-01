package zlc.season.butterfly

class InterceptorController {
    private val interceptorList = mutableListOf<Interceptor>()

    fun addInterceptor(interceptor: Interceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: Interceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(agileRequest: AgileRequest) {
        val temp = mutableListOf<Interceptor>()
        temp.addAll(interceptorList)

        temp.forEach {
            if (it.shouldIntercept(agileRequest)) {
                it.intercept(agileRequest)
            }
        }
    }
}