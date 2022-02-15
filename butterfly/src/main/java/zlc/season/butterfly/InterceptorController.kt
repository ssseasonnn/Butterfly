package zlc.season.butterfly

//class InterceptorController {
//    private val interceptorList = mutableListOf<Interceptor>()
//
//    fun addInterceptor(interceptor: Interceptor) {
//        interceptorList.add(interceptor)
//    }
//
//    suspend fun intercept(butterflyRequest: ButterflyRequest): ButterflyRequest {
//        var newRequest = butterflyRequest
//        interceptorList.forEach {
//            newRequest = it.intercept(newRequest)
//        }
//        return newRequest
//    }
//}