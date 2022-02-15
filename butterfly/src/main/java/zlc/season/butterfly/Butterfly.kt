package zlc.season.butterfly

import android.content.Intent
import zlc.season.butterfly.annotation.Module

object Butterfly {
    private val moduleController by lazy { ModuleController() }

    fun init(vararg module: Module) {
        moduleController.addModule(*module)
    }

    fun addModule(module: Module) {
        moduleController.addModule(module)
    }

    fun removeModule(module: Module) {
        moduleController.removeModule(module)
    }

    fun agile(scheme: String): AgileRequest {
        val className = moduleController.queryAgile(scheme)
        return AgileRequest(scheme, className)
    }

    fun evade(scheme: String): EvadeRequest {
        val (className, implClassName, isSingleton) = moduleController.queryEvade(scheme)
        return EvadeRequest(scheme, className, implClassName, isSingleton)
    }

    fun AgileRequest.carry(onResult: (Intent) -> Unit = {}): Any {
        return AgileDispatcher.dispatch(this, onResult)
    }


    fun EvadeRequest.carry(): Any {
        return EvadeDispatcher.dispatch(this)
    }
}