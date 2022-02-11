package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.first
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import zlc.season.claritypotion.ClarityPotion.Companion.currentActivity
import java.lang.reflect.Proxy

object ButterflyDispatcher {
    const val SCHEME = "butterfly_scheme"

    private const val AGILE_TYPE_NONE = 0
    private const val AGILE_TYPE_ACTIVITY = 1
    private const val AGILE_TYPE_FRAGMENT = 2
    private const val AGILE_TYPE_DIALOG_FRAGMENT = 3
    private const val AGILE_TYPE_SERVICE = 4

    private val interceptorController by lazy { InterceptorController() }

    suspend fun dispatch(request: ButterflyRequest): Any {
        val newRequest = interceptorController.intercept(request)

        if (newRequest is ButterflyRequest.AgileRequest) {
            return dispatchAgile(newRequest)
        } else if (newRequest is ButterflyRequest.EvadeRequest) {
            return dispatchEvade(newRequest)
        } else {
            return Any()
        }
    }

    suspend fun dispatchAgile(request: ButterflyRequest.AgileRequest): Any {
        val context = currentActivity() ?: clarityPotion
        val cls = Class.forName(request.className)
        val type = getAgileType(cls)
        if (type == AGILE_TYPE_ACTIVITY) {
            val intent = createIntent(context, request)
            return when (context) {
                !is Activity -> {
                    clarityPotion.startActivity(intent)
                    Any()
                }
                is FragmentActivity -> {
                    val fm = context.supportFragmentManager
                    ButterflyFragment.showAsFlow(fm, intent).first()
                }
                else -> {
                    Any()
                }
            }
        } else if (type == AGILE_TYPE_SERVICE) {
            val service = cls.newInstance() as Service
            service.start(context, request)
        }

        return Any()
    }

    private fun getAgileType(cls: Class<*>): Int {
        return when {
            Service::class.java.isAssignableFrom(cls) -> AGILE_TYPE_SERVICE
            DialogFragment::class.java.isAssignableFrom(cls) -> AGILE_TYPE_DIALOG_FRAGMENT
            Fragment::class.java.isAssignableFrom(cls) -> AGILE_TYPE_FRAGMENT
            Activity::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTIVITY
            else -> AGILE_TYPE_NONE
        }
    }

    suspend fun dispatchEvade(request: ButterflyRequest.EvadeRequest): Any {
        val evadeClass = Class.forName(request.className)
        if (request.implClassName.isEmpty()) {
            return Any()
        }
        val implClass = Class.forName(request.implClassName)
        val implObj = implClass.newInstance()

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

    private fun createArgsClassArray(args: Array<Any>): Array<Class<*>> {
        val argsClassList = mutableListOf<Class<*>>()
        args.forEach {
            argsClassList.add(it.javaClass)
        }
        return argsClassList.toTypedArray()
    }

    private fun createIntent(context: Context, request: ButterflyRequest.AgileRequest): Intent {
        val intent = Intent()
        intent.putExtra(SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.className)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

}