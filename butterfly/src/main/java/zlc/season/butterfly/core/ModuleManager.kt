package zlc.season.butterfly.core

import zlc.season.butterfly.entities.EvadeData
import zlc.season.butterfly.module.Module

class ModuleManager {
    private val modules = mutableListOf<Module>()

    fun addModule(vararg module: Module) {
        module.forEach {
            modules.add(it)
        }
    }

    fun removeModule(module: Module) {
        modules.remove(module)
    }

    fun queryDestination(route: String): String {
        var result = ""
        modules.forEach {
            val find = it.getDestination()[route]
            if (find != null) {
                result = find.name
                return@forEach
            }
        }
        return result
    }

    fun queryEvade(identity: String): EvadeData {
        var className = ""
        var implClassName = ""
        var isSingleton = true

        modules.forEach {
            if (className.isEmpty()) {
                val evadeMap = it.getEvade()
                val temp = evadeMap[identity]
                if (temp != null) {
                    className = temp.name
                }
            }
            if (implClassName.isEmpty()) {
                val implMap = it.getEvadeImpl()
                val temp = implMap[identity]
                if (temp != null) {
                    implClassName = temp.cls.name
                    isSingleton = temp.singleton
                }
            }

            if (className.isNotEmpty() && implClassName.isNotEmpty()) {
                return@forEach
            }
        }
        return EvadeData(identity, className, implClassName, isSingleton)
    }
}