package zlc.season.butterfly

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class InterceptorController : CoroutineScope by MainScope() {
    private val interceptorList = mutableListOf<Interceptor>()

    fun addInterceptor(interceptor: Interceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: Interceptor) {
        interceptorList.remove(interceptor)
    }

    fun intercept(agileRequest: AgileRequest, afterIntercept: () -> Unit) {
        launch {
            val temp = mutableListOf<Interceptor>()
            temp.addAll(interceptorList)

            temp.forEach {
                if (it.shouldIntercept(agileRequest)) {
                    it.intercept(agileRequest)
                }
            }

            afterIntercept()
        }
    }
}