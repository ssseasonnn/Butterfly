package zlc.season.butterfly

import android.content.Context
import android.util.Log
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

    init {
        interceptorController.addInterceptor(ButterflyInterceptor())
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

    internal fun <T> T.logd(tag: String = ""): T {
        val realTag = tag.ifEmpty { "Butterfly" }
        if (this is Throwable) {
            Log.d(realTag, this.message ?: "", this)
        } else {
            Log.d(realTag, this.toString())
        }
        return this
    }
}