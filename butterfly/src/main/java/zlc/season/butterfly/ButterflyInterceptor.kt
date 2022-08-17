package zlc.season.butterfly

interface ButterflyInterceptor {
    suspend fun shouldIntercept(request: AgileRequest): Boolean
    suspend fun intercept(request: AgileRequest)
}

class DefaultButterflyInterceptor(
    private val interceptor: suspend (AgileRequest) -> Unit
) : ButterflyInterceptor {
    override suspend fun shouldIntercept(request: AgileRequest): Boolean {
        return true
    }

    override suspend fun intercept(request: AgileRequest) {
        interceptor(request)
    }
}