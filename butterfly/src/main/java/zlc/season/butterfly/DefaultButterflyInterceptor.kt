package zlc.season.butterfly

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