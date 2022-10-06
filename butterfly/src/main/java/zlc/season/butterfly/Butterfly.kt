package zlc.season.butterfly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

object Butterfly {
    const val RAW_SCHEME = "butterfly_scheme"

    private val EMPTY_LAMBDA: (Bundle) -> Unit = {}

    fun agile(scheme: String): AgileRequest {
        val realScheme = parseScheme(scheme)
        return ButterflyCore.queryAgile(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
    }

    fun AgileRequest.params(vararg pair: Pair<String, Any?>): AgileRequest {
        return apply {
            bundle.putAll(bundleOf(*pair))
        }
    }

    fun AgileRequest.params(bundle: Bundle): AgileRequest {
        return apply { bundle.putAll(bundle) }
    }

    fun AgileRequest.skipGlobalInterceptor(): AgileRequest {
        return copy(enableGlobalInterceptor = false)
    }

    fun AgileRequest.addInterceptor(interceptor: ButterflyInterceptor): AgileRequest {
        return apply { interceptorController.addInterceptor(interceptor) }
    }

    fun AgileRequest.addInterceptor(interceptor: suspend (AgileRequest) -> Unit): AgileRequest {
        return apply {
            interceptorController.addInterceptor(DefaultButterflyInterceptor(interceptor))
        }
    }

    fun AgileRequest.container(containerViewId: Int): AgileRequest {
        return copy(fragmentConfig = fragmentConfig.copy(containerViewId = containerViewId))
    }

    fun AgileRequest.tag(tag: String): AgileRequest {
        return copy(fragmentConfig = fragmentConfig.copy(tag = tag))
    }

    fun AgileRequest.clearTop(): AgileRequest {
        return copy(
            activityConfig = activityConfig.copy(clearTop = true),
            fragmentConfig = fragmentConfig.copy(clearTop = true)
        )
    }

    fun AgileRequest.disableBackStack(): AgileRequest {
        return copy(fragmentConfig = fragmentConfig.copy(enableBackStack = false))
    }

    fun AgileRequest.addFlag(flag: Int): AgileRequest {
        return copy(activityConfig = activityConfig.copy(flags = activityConfig.flags or flag))
    }

    fun AgileRequest.enterAnim(enterAnim: Int = 0, popEnterAnim: Int = 0): AgileRequest {
        return copy(
            activityConfig = activityConfig.copy(enterAnim = enterAnim),
            fragmentConfig = fragmentConfig.copy(enterAnim = enterAnim, popEnterAnim = popEnterAnim)
        )
    }

    fun AgileRequest.exitAnim(exitAnim: Int = 0, popExitAnim: Int = 0): AgileRequest {
        return copy(
            activityConfig = activityConfig.copy(exitAnim = exitAnim),
            fragmentConfig = fragmentConfig.copy(exitAnim = exitAnim, popExitAnim = popExitAnim)
        )
    }

    fun AgileRequest.flow(): Flow<Unit> {
        return ButterflyCore.dispatchAgile(copy(needResult = false)).flatMapConcat { flowOf(Unit) }
    }

    fun AgileRequest.resultFlow(): Flow<Result<Bundle>> {
        return ButterflyCore.dispatchAgile(copy(needResult = true))
    }

    fun AgileRequest.carry(
        onError: (Throwable) -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        carry(ButterflyHelper.scope, onError, onResult)
    }

    fun AgileRequest.carry(
        scope: CoroutineScope = ButterflyHelper.scope,
        onError: (Throwable) -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        if (onResult == EMPTY_LAMBDA) {
            flow().launchIn(scope)
        } else {
            resultFlow().onEach {
                if (it.isSuccess) {
                    onResult(it.getOrDefault(Bundle()))
                } else {
                    onError(it.exceptionOrNull() ?: Throwable())
                }
            }.launchIn(scope)
        }
    }

    fun AgileRequest.retreat() {
        ButterflyCore.dispatchRetreat(this)
    }

    fun AgileRequest.retreatWithResult(vararg pair: Pair<String, Any?>) {
        ButterflyCore.dispatchRetreat(this, bundleOf(*pair))
    }

    fun Activity.setResult(vararg pair: Pair<String, Any?>) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtras(bundleOf(*pair)) })
    }

    fun Activity.setResult(bundle: Bundle) {
        setResult(Activity.RESULT_OK, Intent().apply { putExtras(bundle) })
    }

    fun Activity.retreat() {
        finish()
    }

    fun Activity.retreatWithResult(vararg pair: Pair<String, Any?>) {
        setResult(*pair)
        finish()
    }

    fun Fragment.setResult(vararg pair: Pair<String, Any?>) {
        parentFragmentManager.setFragmentResult(javaClass.name, bundleOf(*pair))
    }

    fun Fragment.setResult(bundle: Bundle) {
        parentFragmentManager.setFragmentResult(javaClass.name, bundle)
    }

    fun Fragment.retreat() {
        parentFragmentManager.popBackStack()
    }

    fun Fragment.retreatWithResult(vararg pair: Pair<String, Any?>) {
        setResult(*pair)
        parentFragmentManager.popBackStack()
    }

    val EVADE_LAMBDA: (String, Class<*>) -> Any = { identity, cls ->
        val real = identity.ifEmpty { cls.simpleName }
        var request = ButterflyCore.queryEvade(real)
        if (request.className.isEmpty()) {
            request = request.copy(className = cls.name)
        }
        ButterflyCore.dispatchEvade(request)
    }

    inline fun <reified T> evade(
        identity: String = "",
        noinline func: (String, Class<*>) -> Any = EVADE_LAMBDA
    ): T {
        return func(identity, T::class.java) as T
    }
}