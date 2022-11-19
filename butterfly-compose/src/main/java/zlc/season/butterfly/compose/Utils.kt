package zlc.season.butterfly.compose

import android.app.Activity
import zlc.season.butterfly.backstack.BackStackEntry
import zlc.season.butterfly.group.GroupEntry

object Utils {
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
}