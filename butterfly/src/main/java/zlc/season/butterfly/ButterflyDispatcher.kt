package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import zlc.season.claritypotion.ClarityPotion.Companion.currentActivity
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.Objects.*

class AgileDispatcher {
    companion object {
        const val RAW_SCHEME = "butterfly_scheme"

        private const val AGILE_TYPE_NONE = 0
        private const val AGILE_TYPE_ACTION = 1
        private const val AGILE_TYPE_ACTIVITY = 2
    }

    fun dispatch(request: AgileRequest, needResult: Boolean): Flow<Result<Intent>> {
        if (request.className.isEmpty()) {
            "Agile --> class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Agile class not found!")))
        }

        val cls = Class.forName(request.className)
        return when (getAgileType(cls)) {
            AGILE_TYPE_ACTIVITY -> dispatchActivity(request, needResult)
            AGILE_TYPE_ACTION -> dispatchAction(request)
            else -> {
                "Agile --> type error".logw()
                flowOf(Result.failure(IllegalStateException("Agile type error")))
            }
        }
    }

    private fun dispatchActivity(request: AgileRequest, needResult: Boolean): Flow<Result<Intent>> {
        return if (!needResult) {
            val context = getContext()
            val intent = createIntent(getContext(), request)
            context.startActivity(intent)
            emptyFlow()
        } else {
            val currentActivity = currentActivity()
            return if (currentActivity != null && currentActivity is FragmentActivity) {
                val intent = createIntent(currentActivity, request)
                ButterflyFragment.showAsFlow(currentActivity.supportFragmentManager, intent)
            } else {
                "Agile --> activity not found".logw()
                flowOf(Result.failure(IllegalStateException("Activity not found")))
            }
        }
    }

    private fun dispatchAction(request: AgileRequest): Flow<Result<Intent>> {
        val context = getContext()
        val cls = Class.forName(request.className)
        val action = cls.newInstance() as Action
        action.doAction(context, request.scheme, request.bundle)
        return emptyFlow()
    }

    private fun getAgileType(cls: Class<*>): Int {
        return when {
            Action::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTION
            Activity::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTIVITY
            else -> AGILE_TYPE_NONE
        }
    }

    private fun createIntent(context: Context, request: AgileRequest): Intent {
        val intent = Intent()
        intent.putExtra(RAW_SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.className)
        intent.putExtras(request.bundle)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    private fun getContext(): Context = currentActivity() ?: clarityPotion
}

class EvadeDispatcher {
    private val implObjMap = mutableMapOf<String, Any>()

    fun dispatch(request: EvadeRequest): Any {
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

    private fun check(request: EvadeRequest): Boolean {
        if (request.implClassName.isEmpty()) {
            return false
        }
        return true
    }

    private fun Class<*>.getImplObj(request: EvadeRequest): Any {
        return if (request.isSingleton) {
            realGetImplObj(request)
        } else {
            newInstance()
        }
    }

    @Synchronized
    private fun Class<*>.realGetImplObj(request: EvadeRequest): Any {
        var find = implObjMap[request.implClassName]
        if (find == null) {
            val implObj = newInstance()
            implObjMap[request.implClassName] = implObj
            find = implObj
        }
        return find!!
    }

    private fun createProxyObj(cls: Class<*>, implObj: Any = Any(), block: (Method, Array<Any>?) -> Any? = { _, _ -> }): Any {
        return Proxy.newProxyInstance(Thread.currentThread().contextClassLoader, arrayOf(cls)) { _, method, args ->
            try {
                if (method.name == "hashCode") {
                    hashCode(implObj)
                } else if (method.name == "toString") {
                    toString(implObj)
                } else if (method.name == "equals") {
                    val other = args[0]
                    if (hashCode(implObj) == other.hashCode()) {
                        true
                    } else {
                        equals(implObj, other)
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