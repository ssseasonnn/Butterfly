package zlc.season.butterfly

interface Interceptor {
    fun shouldIntercept(agileRequest: AgileRequest): Boolean

    suspend fun intercept(agileRequest: AgileRequest)
}