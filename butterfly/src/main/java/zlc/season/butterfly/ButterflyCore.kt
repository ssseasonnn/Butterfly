@file:OptIn(FlowPreview::class)

package zlc.season.butterfly

import android.os.Bundle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import zlc.season.butterfly.dispatcher.AgileDispatcher
import zlc.season.butterfly.dispatcher.EvadeDispatcher
import zlc.season.butterfly.module.Module

object ButterflyCore {
    private val moduleManager by lazy { ModuleManager() }
    private val interceptorManager by lazy { InterceptorManager() }
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

    fun addModule(module: Module) = moduleManager.addModule(module)

    fun removeModule(module: Module) = moduleManager.removeModule(module)

    fun addInterceptor(interceptor: ButterflyInterceptor) = interceptorManager.addInterceptor(interceptor)

    fun removeInterceptor(interceptor: ButterflyInterceptor) = interceptorManager.removeInterceptor(interceptor)

    fun queryAgile(scheme: String): AgileRequest = moduleManager.queryAgile(scheme)

    fun queryEvade(identity: String): EvadeRequest = moduleManager.queryEvade(identity)

    fun dispatchAgile(request: AgileRequest, interceptorManager: InterceptorManager): Flow<Result<Bundle>> {
        return flowOf(Unit).onEach {
            if (request.enableGlobalInterceptor) {
                interceptorManager.intercept(request)
            }
        }.onEach {
            interceptorManager.intercept(request)
        }.flatMapConcat {
            agileDispatcher.dispatch(request)
        }
    }

    fun dispatchRetreat(bundle: Bundle): AgileRequest? {
        return agileDispatcher.retreat(bundle)
    }

    fun dispatchEvade(evadeRequest: EvadeRequest): Any {
        return evadeDispatcher.dispatch(evadeRequest)
    }
}