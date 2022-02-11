package zlc.season.butterfly

import zlc.season.butterfly.annotation.Module

object ButterflyCore {
    const val TYPE_AGILE = 0
    const val TYPE_EVADE = 1

    private val moduleController by lazy { ModuleController() }

    fun init(vararg module: Module) {
        moduleController.addModule(*module)
    }

    fun addModule(module: Module) {
        moduleController.addModule(module)
    }

    fun removeModule(module: Module) {
    }

    fun queryAgile(scheme: String): String {
        return moduleController.queryAgile(scheme)
    }

    fun queryEvade(scheme: String): Triple<String, String, Boolean> {
        return moduleController.queryEvade(scheme)
    }

}