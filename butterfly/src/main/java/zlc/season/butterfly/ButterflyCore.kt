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

    fun dispatchAgile(agileRequest: AgileRequest): Flow<Result<Bundle>> {
        return flowOf(Unit).onEach {
            if (agileRequest.enableGlobalInterceptor) {
                interceptorController.intercept(agileRequest)
            }
        }.onEach {
            agileRequest.interceptorController.intercept(agileRequest)
        }.flatMapConcat {
            agileDispatcher.dispatch(agileRequest)
        }
    }

    fun dispatchRetreat(cls: Class<*>, bundle: Bundle): Boolean {
        return agileDispatcher.retreat(cls, bundle)
    }

    fun dispatchRetreatDirectly(cls: Class<*>, target: Any, bundle: Bundle): Boolean {
        return agileDispatcher.retreatDirectly(cls, target, bundle)
    }

    fun getRetreatCount(cls: Class<*>): Int {
        return agileDispatcher.retreatCount(cls)
    }

    fun dispatchEvade(evadeRequest: EvadeRequest): Any {
        return evadeDispatcher.dispatch(evadeRequest)
    }
}