package zlc.season.butterfly.dispatcher

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
import zlc.season.butterfly.*
import zlc.season.butterfly.Butterfly.setResult
import zlc.season.butterfly.ButterflyHelper
import zlc.season.butterfly.ButterflyHelper.activity
import zlc.season.butterfly.ButterflyHelper.awaitFragmentResult
import zlc.season.butterfly.ButterflyHelper.context
import zlc.season.butterfly.ButterflyHelper.fragmentActivity
import zlc.season.butterfly.ButterflyHelper.fragmentManager
import zlc.season.butterfly.parseScheme

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

    suspend fun dispatch(request: AgileRequest): Flow<Result<Bundle>> {
        if (request.className.isEmpty()) {
            "Agile --> class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Agile class not found!")))
        }

        val cls = Class.forName(request.className)
        return dispatcherMap[getAgileType(cls)]!!.dispatch(request)
    }

    fun retreat(request: AgileRequest, bundle: Bundle) {
        if (request.className.isEmpty()) {
            "Agile --> class not found!".logw()
            return
        }

        val cls = Class.forName(request.className)
        return dispatcherMap[getAgileType(cls)]!!.retreat(request, bundle)
    }
}