package zlc.season.butterfly

interface ButterflyInterceptor {
    suspend fun shouldIntercept(request: AgileRequest): Boolean
    suspend fun intercept(request: AgileRequest): AgileRequest
}