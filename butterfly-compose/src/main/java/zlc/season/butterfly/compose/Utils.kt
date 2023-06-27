package zlc.season.butterfly.compose

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.internal.ButterflyHelper.contentView

object Utils {
    const val COMPOSE_VIEW_TAG = "Butterfly_Compose_View_Tag"

    fun isComposeEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return AgileComposable::class.java.isAssignableFrom(cls)
    }

    fun isComposeEntry(entry: GroupEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return AgileComposable::class.java.isAssignableFrom(cls)
    }

    fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return Activity::class.java.isAssignableFrom(cls)
    }

    fun Activity.findContainerView(request: AgileRequest): ViewGroup {
        var result: ViewGroup? = null
        if (request.containerViewId != 0) {
            result = findViewById(request.containerViewId)
        } else if (request.containerViewTag.isNotEmpty()) {
            result = window.decorView.findViewWithTag(request.containerViewTag)
        }
        if (result == null) {
            result = contentView()
        }
        return result
    }

    fun Activity.clearContainerView(request: AgileRequest) {
        val container = findContainerView(request)
        val composeView = container.findViewWithTag<ComposeView>(COMPOSE_VIEW_TAG)
        composeView?.setContent { }
    }

    fun BackStackEntry.hasContainer() =
        request.containerViewId != 0 || request.containerViewTag.isNotEmpty()

    fun BackStackEntry.hasSameContainer(other: BackStackEntry) =
        request.containerViewId == other.request.containerViewId &&
                request.containerViewTag == other.request.containerViewTag
}