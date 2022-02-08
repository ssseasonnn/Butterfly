package zlc.season.butterfly

import zlc.season.butterfly.annotation.Module

class ButterflyModuleController {
    private val modules = mutableListOf<Module>()

    fun addModule(vararg module: Module) {
        module.forEach {
            modules.add(it)
        }
    }

    fun query(scheme: String): String {
        var result = ""
        modules.forEach {
            val find = it.get()[scheme]
            if (!find.isNullOrEmpty()) {
                result = find
                return@forEach
            }
        }
        return result
    }
}