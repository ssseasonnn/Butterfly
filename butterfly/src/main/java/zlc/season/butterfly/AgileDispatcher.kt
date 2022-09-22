package zlc.season.butterfly

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.ButterflyHelper.context
import zlc.season.butterfly.ButterflyHelper.fragmentActivity
import zlc.season.butterfly.ButterflyHelper.fragmentManager

class AgileDispatcher {
    companion object {
        private const val AGILE_TYPE_NONE = 0
        private const val AGILE_TYPE_ACTION = 1
        private const val AGILE_TYPE_ACTIVITY = 2
        private const val AGILE_TYPE_DIALOG_FRAGMENT = 3
        private const val AGILE_TYPE_FRAGMENT = 4
    }

    private val dispatcherMap = mapOf(
        AGILE_TYPE_NONE to NoneDispatcher,
        AGILE_TYPE_ACTION to ActionDispatcher,
        AGILE_TYPE_ACTIVITY to ActivityDispatcher,
        AGILE_TYPE_DIALOG_FRAGMENT to DialogFragmentDispatcher,
        AGILE_TYPE_FRAGMENT to FragmentDispatcher
    )

    private fun getAgileType(cls: Class<*>): Int {
        return when {
            Action::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTION
            Activity::class.java.isAssignableFrom(cls) -> AGILE_TYPE_ACTIVITY
            DialogFragment::class.java.isAssignableFrom(cls) -> AGILE_TYPE_DIALOG_FRAGMENT
            Fragment::class.java.isAssignableFrom(cls) -> AGILE_TYPE_FRAGMENT
            else -> AGILE_TYPE_NONE
        }
    }

    fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        if (request.className.isEmpty()) {
            "Agile --> class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Agile class not found!")))
        }

        val cls = Class.forName(request.className)
        return dispatcherMap[getAgileType(cls)]!!.dispatch(request)
    }
}

interface InnerDispatcher {
    fun dispatch(request: AgileRequest): Flow<Result<Bundle>>
}

object NoneDispatcher : InnerDispatcher {
    override fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        "Agile --> type error".logw()
        return flowOf(Result.failure(IllegalStateException("Agile type error")))
    }
}

object ActionDispatcher : InnerDispatcher {
    override fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val cls = Class.forName(request.className)
        val action = cls.newInstance() as Action
        action.doAction(context, request.scheme, request.bundle)
        return flowOf(Result.success(Bundle()))
    }
}

object ActivityDispatcher : InnerDispatcher {
    override fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        return if (!request.needResult) {
            val context = context
            val intent = createIntent(context, request)
            context.startActivity(intent, createActivityOptions(context, request)?.toBundle())

            flowOf(Result.success(Bundle()))
        } else {
            val fm = fragmentManager
            if (fm != null) {
                val context = context
                val intent = createIntent(context, request)
                ButterflyFragment.showAsFlow(fm, intent, createActivityOptions(context, request))
            } else {
                "Agile --> activity not found".logw()
                flowOf(Result.failure(IllegalStateException("Activity not found")))
            }
        }
    }

    private fun createIntent(context: Context, request: AgileRequest): Intent {
        val intent = Intent()
        intent.putExtra(Butterfly.RAW_SCHEME, request.scheme)
        intent.setClassName(context.packageName, request.className)
        intent.putExtras(request.bundle)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }

    private fun createActivityOptions(
        context: Context,
        request: AgileRequest
    ): ActivityOptionsCompat? {
        val config = request.activityConfig
        return config.activityOptions
            ?: if (config.enterAnim != 0 || config.exitAnim != 0) {
                ActivityOptionsCompat.makeCustomAnimation(
                    context,
                    config.enterAnim,
                    config.exitAnim
                )
            } else {
                null
            }
    }
}

object DialogFragmentDispatcher : InnerDispatcher {
    override fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = fragmentActivity
            ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        val fragment = createFragment(activity, request)
        val fragmentManager = activity.supportFragmentManager

        if (fragment is DialogFragment) {
            fragment.show(fragmentManager, fragment.javaClass.name)
        }
        return flowOf(Result.success(Bundle()))
    }

    private fun createFragment(
        activity: FragmentActivity,
        request: AgileRequest
    ): Fragment {
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            activity.classLoader,
            request.className
        )
        fragment.arguments = request.bundle
        return fragment
    }
}

object FragmentDispatcher : InnerDispatcher {
    override fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        val activity = fragmentActivity
            ?: return flowOf(Result.failure(IllegalStateException("Activity not found")))

        val fragment = createFragment(activity, request)
        val fragmentManager = activity.supportFragmentManager
        fragmentManager.commit(fragment, request)
        return if (request.needResult) {
            fragmentManager.awaitFragmentResult(activity, fragment)
        } else {
            flowOf(Result.success(Bundle()))
        }
    }

    private fun FragmentManager.commit(
        fragment: Fragment,
        request: AgileRequest
    ) = with(beginTransaction()) {
        val config = request.fragmentConfig
        setCustomAnimations(
            config.enterAnim,
            config.exitAnim,
            config.popEnterAnim,
            config.popExitAnim
        )

        if (config.addToBackStack && isAddToBackStackAllowed) {
            addToBackStack(null)
        }

        if (config.isAdd) {
            add(android.R.id.content, fragment)
        } else {
            replace(android.R.id.content, fragment)
        }

        if (isStateSaved) {
            commitAllowingStateLoss()
        } else {
            commit()
        }
    }


    private fun createFragment(
        activity: FragmentActivity,
        request: AgileRequest
    ): Fragment {
        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            activity.classLoader,
            request.className
        )
        fragment.arguments = request.bundle
        return fragment
    }
}