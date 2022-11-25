@file:OptIn(FlowPreview::class)

package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import zlc.season.butterfly.dispatcher.AgileDispatcher
import zlc.season.butterfly.dispatcher.EvadeDispatcher
import zlc.season.butterfly.module.Module

object ButterflyCore {
    private val moduleManager = ModuleManager()
    private val interceptorManager = InterceptorManager()
    private val agileDispatcher = AgileDispatcher()
    private val evadeDispatcher = EvadeDispatcher()

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

    fun dispatchAgile(
        context: Context,
        request: AgileRequest,
        interceptorManager: InterceptorManager
    ): Flow<Result<Bundle>> {
        return flowOf(request)
            .map {
                if (it.enableGlobalInterceptor) {
                    this.interceptorManager.intercept(it)
                } else {
                    it
                }
            }
            .map {
                interceptorManager.intercept(it)
            }
            .flatMapConcat {
                agileDispatcher.dispatch(context, it)
            }
    }

    fun dispatchRetreat(bundle: Bundle): AgileRequest? {
        return agileDispatcher.retreat(bundle)
    }

    fun dispatchEvade(evadeRequest: EvadeRequest): Any {
        return evadeDispatcher.dispatch(evadeRequest)
    }
}