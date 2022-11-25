package zlc.season.butterfly

class DefaultButterflyInterceptor(
    private val interceptor: suspend (AgileRequest) -> AgileRequest
) : ButterflyInterceptor {
    override suspend fun shouldIntercept(request: AgileRequest): Boolean {
        return true
    }

    override suspend fun intercept(request: AgileRequest): AgileRequest {
        return interceptor(request)
    }
}