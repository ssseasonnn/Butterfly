package zlc.season.butterfly

interface ButterflyInterceptor {
    fun shouldIntercept(agileRequest: AgileRequest): Boolean

    suspend fun intercept(agileRequest: AgileRequest)
}