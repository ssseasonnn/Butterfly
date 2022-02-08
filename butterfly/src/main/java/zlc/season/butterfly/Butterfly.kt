package zlc.season.butterfly

import zlc.season.butterfly.annotation.Module

object Butterfly {
    private val moduleController by lazy { ButterflyModuleController() }

    fun init(vararg module: Module) {
        moduleController.addModule(*module)
    }

    fun evade(scheme: String) {
        val dest = moduleController.query(scheme)
        if (dest.isNotEmpty()) {

        }else{
            //log
        }
    }
}