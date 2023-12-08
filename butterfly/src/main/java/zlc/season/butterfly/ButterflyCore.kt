package zlc.season.butterfly

import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.core.EvadeManager
import zlc.season.butterfly.core.InterceptorManager
import zlc.season.butterfly.core.ModuleManager
import zlc.season.butterfly.core.NavigatorManager
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.entities.EvadeData
import zlc.season.butterfly.interceptor.Interceptor
import zlc.season.butterfly.module.Module

object ButterflyCore {
    private val moduleManager = ModuleManager()
    private val interceptorManager = InterceptorManager()
    private val navigatorManager = NavigatorManager()
    private val evadeManager = EvadeManager()

    fun addModuleName(moduleName: String) {
        try {
            val cls = Class.forName(moduleName)
            if (Module::class.java.isAssignableFrom(cls)) {
                val module = cls.getDeclaredConstructor().newInstance() as Module
                addModule(module)
            }
        } catch (ignore: Exception) {
            //ignore
        }
    }

    fun addModule(module: Module) = moduleManager.addModule(module)

    fun removeModule(module: Module) = moduleManager.removeModule(module)

    fun addInterceptor(interceptor: Interceptor) = interceptorManager.addInterceptor(interceptor)

    fun removeInterceptor(interceptor: Interceptor) =
        interceptorManager.removeInterceptor(interceptor)

    fun queryDestination(scheme: String): String = moduleManager.queryDestination(scheme)

    fun queryEvade(identity: String): EvadeData = moduleManager.queryEvade(identity)

    suspend fun dispatchDestination(
        context: Context,
        destinationData: DestinationData,
        interceptorManager: InterceptorManager
    ): Result<Bundle> {
        var tempDestinationData = if (destinationData.enableGlobalInterceptor) {
            this.interceptorManager.intercept(destinationData)
        } else {
            destinationData
        }

        tempDestinationData = interceptorManager.intercept(tempDestinationData)

        return navigatorManager.navigate(context, tempDestinationData)
    }

    fun popBack(context: Context, bundle: Bundle): DestinationData? {
        return navigatorManager.popBack(context, bundle)
    }

    fun dispatchEvade(evadeData: EvadeData): Any {
        return evadeManager.dispatch(evadeData)
    }
}