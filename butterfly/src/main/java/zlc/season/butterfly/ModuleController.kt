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

    fun queryAgile(scheme: String): AgileRequest {
        var result = ""
        modules.forEach {
            val find = it.getAgile()[scheme]
            if (!find.isNullOrEmpty()) {
                result = find
                return@forEach
            }
        }
        return AgileRequest(scheme, result)
    }

    fun queryEvade(identity: String): EvadeRequest {
        var className = ""
        var implClassName = ""
        var isSingleton = true

        modules.forEach {
            if (className.isEmpty()) {
                val evadeMap = it.getEvade()
                val temp = evadeMap[identity]
                if (!temp.isNullOrEmpty()) {
                    className = temp
                }
            }
            if (implClassName.isEmpty()) {
                val implMap = it.getEvadeImpl()
                val temp = implMap[identity]
                if (temp != null) {
                    implClassName = temp.className
                    isSingleton = temp.singleton
                }
            }

            if (className.isNotEmpty() && implClassName.isNotEmpty()) {
                return@forEach
            }
        }
        return EvadeRequest(identity, className, implClassName, isSingleton)
    }
}