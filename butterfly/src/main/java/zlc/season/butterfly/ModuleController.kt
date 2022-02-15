package zlc.season.butterfly

import zlc.season.butterfly.annotation.Module

class ModuleController {
    private val modules = mutableListOf<Module>()

    fun addModule(vararg module: Module) {
        module.forEach {
            modules.add(it)
        }
    }

    fun removeModule(module: Module) {
        modules.remove(module)
    }

    fun queryAgile(scheme: String): String {
        var result = ""
        modules.forEach {
            val find = it.getAgile()[scheme]
            if (!find.isNullOrEmpty()) {
                result = find
                return@forEach
            }
        }
        return result
    }

    fun queryEvade(scheme: String): Triple<String, String, Boolean> {
        var className = ""
        var implClassName = ""
        var isSingleton = false
        modules.forEach {
            if (className.isEmpty()) {
                val evadeMap = it.getEvade()
                val temp = evadeMap[scheme]
                if (!temp.isNullOrEmpty()) {
                    className = temp
                }
            }
            if (implClassName.isEmpty()) {
                val implMap = it.getEvadeImpl()
                val temp = implMap[scheme]
                if (temp != null) {
                    implClassName = temp.className
                    isSingleton = temp.singleton
                }
            }

            if (className.isNotEmpty() && implClassName.isNotEmpty()) {
                return@forEach
            }
        }
        return Triple(className, implClassName, isSingleton)
    }
}