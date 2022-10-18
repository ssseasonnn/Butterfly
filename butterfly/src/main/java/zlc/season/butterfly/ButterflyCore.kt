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

    fun addInterceptor(interceptor: ButterflyInterceptor) = interceptorController.addInterceptor(interceptor)

    fun removeInterceptor(interceptor: ButterflyInterceptor) = interceptorController.removeInterceptor(interceptor)

    fun queryAgile(scheme: String): AgileRequest = moduleController.queryAgile(scheme)

    fun queryEvade(identity: String): EvadeRequest = moduleController.queryEvade(identity)

    fun dispatchAgile(handler: AgileRequestHandler): Flow<Result<Bundle>> {
        return flowOf(Unit).onEach {
            if (handler.request.enableGlobalInterceptor) {
                interceptorController.intercept(handler.request)
            }
        }.onEach {
            handler.interceptorController.intercept(handler.request)
        }.flatMapConcat {
            agileDispatcher.dispatch(handler.request)
        }
    }

    fun dispatchRetreat(target: Any?, bundle: Bundle): Boolean {
        return agileDispatcher.retreat(target, bundle)
    }

    fun dispatchEvade(evadeRequest: EvadeRequest): Any {
        return evadeDispatcher.dispatch(evadeRequest)
    }
}