package zlc.season.butterfly.compose

import android.app.Activity
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import zlc.season.butterfly.entities.BackStackEntry
import zlc.season.butterfly.entities.DestinationData
import zlc.season.butterfly.entities.GroupEntry
import zlc.season.butterfly.internal.ButterflyHelper.contentView

object Utils {
    const val COMPOSE_VIEW_TAG = "Butterfly_Compose_View_Tag"

    fun isComposeEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.destinationData.className)
        return ComposeDestination::class.java.isAssignableFrom(cls)
    }

    fun isComposeEntry(entry: GroupEntry): Boolean {
        val cls = Class.forName(entry.destinationData.className)
        return ComposeDestination::class.java.isAssignableFrom(cls)
    }

    fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.destinationData.className)
        return Activity::class.java.isAssignableFrom(cls)
    }

    fun Activity.findContainerView(data: DestinationData): ViewGroup {
        var result: ViewGroup? = null
        if (data.containerViewId != 0) {
            result = findViewById(data.containerViewId)
        } else if (data.containerViewTag.isNotEmpty()) {
            result = window.decorView.findViewWithTag(data.containerViewTag)
        }
        if (result == null) {
            result = contentView()
        }
        return result
    }

    fun Activity.getComposeView(data: DestinationData): ComposeView? {
        val containerView = findContainerView(data)
        return containerView.findViewWithTag(COMPOSE_VIEW_TAG)
    }

    fun Activity.clearContainerView(data: DestinationData) {
        val composeView = getComposeView(data)
        composeView?.setContent { }
    }

    fun BackStackEntry.hasCustomContainer() =
        destinationData.containerViewId != 0 || destinationData.containerViewTag.isNotEmpty()

    fun BackStackEntry.hasSameCustomContainer(other: BackStackEntry) =
        destinationData.containerViewId == other.destinationData.containerViewId &&
                destinationData.containerViewTag == other.destinationData.containerViewTag
}