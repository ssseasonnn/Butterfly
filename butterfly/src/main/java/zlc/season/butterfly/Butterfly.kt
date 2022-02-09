package zlc.season.butterfly

import android.content.Context
import zlc.season.butterfly.annotation.Module

object Butterfly {
    private val moduleController by lazy { ModuleController() }
    private val dispatchController by lazy { DispatchController() }
    private val interceptorController by lazy { InterceptorController() }

    fun init(vararg module: Module) {
        moduleController.addModule(*module)
    }

    fun addModule(module: Module) {
        moduleController.addModule(module)
    }

    fun removeModule(module: Module) {

    }

    fun agile(scheme: String): Request {
        val dest = moduleController.query(scheme)
        return Request(scheme, dest)
    }


    suspend fun Request.evade(context: Context) {
        val newRequest = interceptorController.intercept(context, this)
        dispatchController.dispatch(context, newRequest)
    }

    suspend fun Request.evaded(context: Context): Result {
        val newRequest = interceptorController.intercept(context, this)
        return dispatchController.dispatchWithResult(context, newRequest)
    }
}