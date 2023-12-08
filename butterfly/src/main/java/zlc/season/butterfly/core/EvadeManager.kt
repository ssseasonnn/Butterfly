package zlc.season.butterfly.core

import zlc.season.butterfly.entities.EvadeData
import zlc.season.butterfly.internal.logw
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.Objects

class EvadeManager {
    private val implObjMap = mutableMapOf<String, Any>()

    fun dispatch(request: EvadeData): Any {
        val evadeClass = Class.forName(request.className)

        if (!check(request)) {
            "Evade -> $request not found!".logw()
            return createProxyObj(evadeClass)
        }

        val implClass = Class.forName(request.implClassName)
        val implObj = implClass.getImplObj(request)

        // check extend
        if (evadeClass.isAssignableFrom(implClass)) {
            return implObj
        }

        return createProxyObj(evadeClass, implObj) { method, args ->
            if (args == null) {
                val findMethod = implClass.getDeclaredMethod(method.name)
                findMethod.invoke(implObj)
            } else {
                val findMethod = implClass.getDeclaredMethod(method.name, *method.parameterTypes)
                findMethod.invoke(implObj, *args)
            }
        }
    }

    private fun check(request: EvadeData): Boolean {
        if (request.implClassName.isEmpty()) {
            return false
        }
        return true
    }

    private fun Class<*>.getImplObj(request: EvadeData): Any {
        return if (request.isSingleton) {
            realGetImplObj(request)
        } else {
            getDeclaredConstructor().newInstance()
        }
    }

    @Synchronized
    private fun Class<*>.realGetImplObj(request: EvadeData): Any {
        var find = implObjMap[request.implClassName]
        if (find == null) {
            val implObj = getDeclaredConstructor().newInstance()
            implObjMap[request.implClassName] = implObj
            find = implObj
        }
        return find!!
    }

    private fun createProxyObj(
        cls: Class<*>,
        implObj: Any = Any(),
        block: (Method, Array<Any>?) -> Any? = { _, _ -> }
    ): Any {
        return Proxy.newProxyInstance(
            Thread.currentThread().contextClassLoader,
            arrayOf(cls)
        ) { _, method, args ->
            try {
                if (method.name == "hashCode") {
                    Objects.hashCode(implObj)
                } else if (method.name == "toString") {
                    Objects.toString(implObj)
                } else if (method.name == "equals") {
                    val other = args[0]
                    if (Objects.hashCode(implObj) == other.hashCode()) {
                        true
                    } else {
                        Objects.equals(implObj, other)
                    }
                } else {
                    block(method, args)
                }
            } catch (e: Exception) {
                if (e is NoSuchMethodException) {
                    "Evade -> Method ${e.message} not found!".logw()
                } else {
                    e.logw()
                }
                Unit
            }
        }
    }
}