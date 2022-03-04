package zlc.season.butterfly

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import zlc.season.butterfly.module.Module

object ButterflyCore {
    private val moduleController by lazy { ModuleController() }
    private val interceptorController by lazy { InterceptorController() }
    private val agileDispatcher by lazy { AgileDispatcher() }
    private val evadeDispatcher by lazy { EvadeDispatcher() }

    fun addModuleName(moduleName: String) {
        try {
            val cls = Class.forName(moduleName)
            if (Module::class.java.isAssignableFrom(cls)) {
                val module = cls.newInstance() as Module
                addModule(module)
            }
        } catch (ignore: Exception) {
            //ignore
        }
    }

    fun addModule(module: Module) = moduleController.addModule(module)

    fun removeModule(module: Module) = moduleController.removeModule(module)

    fun queryAgile(scheme: String): AgileRequest = moduleController.queryAgile(scheme)

    fun queryEvade(identity: String): EvadeRequest = moduleController.queryEvade(identity)

    fun addInterceptor(interceptor: ButterflyInterceptor) = interceptorController.addInterceptor(interceptor)

    fun removeInterceptor(interceptor: ButterflyInterceptor) = interceptorController.removeInterceptor(interceptor)

    fun dispatchAgile(agileRequest: AgileRequest, needResult: Boolean): Flow<Result<Intent>> {
        return flow {
            if (agileRequest.needIntercept) {
                interceptorController.intercept(agileRequest)
            }
            emit(Unit)
        }.flatMapConcat {
            agileDispatcher.dispatch(agileRequest, needResult)
        }
    }

    fun dispatchEvade(evadeRequest: EvadeRequest): Any {
        return evadeDispatcher.dispatch(evadeRequest)
    }
}