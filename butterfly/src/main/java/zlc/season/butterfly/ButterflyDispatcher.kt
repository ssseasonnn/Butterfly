package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import zlc.season.claritypotion.ClarityPotion.Companion.currentActivity
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
object AgileDispatcher {
    const val SCHEME = "butterfly_scheme"

    private const val AGILE_TYPE_NONE = 0
    private const val AGILE_TYPE_ACTION = 1
    private const val AGILE_TYPE_ACTIVITY = 2
    private const val AGILE_TYPE_FRAGMENT = 3

    private val returnObj = Any()

    fun dispatch(request: AgileRequest, onResult: ((Intent) -> Unit)? = null): Any {
        if (request.className.isEmpty()) return returnObj
        val cls = Class.forName(request.className)
        return when (getAgileType(cls)) {
            AGILE_TYPE_ACTIVITY -> {
                dispatchActivity(request, onResult)
                returnObj
            }
            AGILE_TYPE_FRAGMENT -> {
                dispatchFragment(request)
            }
            AGILE_TYPE_ACTION -> {
                dispatchAction(request)
            }
            else -> returnObj
        }
    }

    private fun <T> dispatchActivity(request: AgileRequest, onResult: ((T) -> Unit)? = null) {
        if (onResult == null) {
            val context = currentActivity() ?: clarityPotion
            val intent = createIntent(context, request)
            context.startActivity(intent)
        } else {
            val currentActivity = currentActivity()
            if (currentActivity != null && currentActivity is FragmentActivity) {
                val intent = createIntent(currentActivity, request)
                ButterflyFragment.show(currentActivity.supportFragmentManager, intent) {
                    onResult(it as T)
                }
            } else {
                "No valid activity found!".logd()
            }
        }
    }

    private fun dispatchFragment(request: AgileRequest): Any {
        val cls = Class.forName(request.className)
        return cls.newInstance()
    }

    private fun dispatchAction(request: AgileRequest): Any {
        val context = currentActivity() ?: clarityPotion
        val cls = Class.forName(request.className)
        val action = cls.newInstance() as Action
        return action.doAction(context, request.scheme)
    }

    private fun getAgileType(cls: Class<*>): Int {
        return when {
            Action::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTION
            Activity::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTIVITY
            Fragment::class.java.isAssignableFrom(cls) -> AGILE_TYPE_FRAGMENT
            else -> AGILE_TYPE_NONE
        }
    }

    private fun createIntent(context: Context, request: AgileRequest): Intent {
        val intent = Intent()
        intent.putExtra(SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.className)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
}

object EvadeDispatcher {
    private val implObjMap = ConcurrentHashMap<String, Any>()

    fun dispatch(request: EvadeRequest): Any {
        val evadeClass = Class.forName(request.className)
        if (request.implClassName.isEmpty()) {
            "No evade impl found".logd()
            return Unit
        }

        val implClass = Class.forName(request.implClassName)
        val implObj = implClass.getImplObj(request)

        if (implClass.isAssignableFrom(evadeClass)) {
            return implObj
        }

        val evadeObj = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(evadeClass)) { _, method, args ->
            try {
                if (args == null) {
                    val findMethod = implClass.getDeclaredMethod(method.name)
                    return@newProxyInstance findMethod.invoke(implObj)
                } else {
                    val argsArray = createArgsClassArray(args)
                    val findMethod = implClass.getDeclaredMethod(method.name, *argsArray)
                    return@newProxyInstance findMethod.invoke(implObj, args)
                }
            } catch (e: Exception) {
                e.message.logd()
            }
        }
        return evadeObj
    }

    private fun Class<*>.getImplObj(request: EvadeRequest): Any {
        return if (request.isSingleton) {
            val find = implObjMap[request.implClassName]
            if (find == null) {
                val implObj = newInstance()
                implObjMap[request.implClassName] = implObj
                implObj
            } else {
                find
            }
        } else {
            newInstance()
        }
    }

    private fun createArgsClassArray(args: Array<Any>): Array<Class<*>> {
        val argsClassList = mutableListOf<Class<*>>()
        args.forEach {
            argsClassList.add(it.javaClass)
        }
        return argsClassList.toTypedArray()
    }
}