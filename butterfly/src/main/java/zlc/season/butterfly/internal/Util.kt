package zlc.season.butterfly.internal

import android.app.Activity
import androidx.core.net.toUri
import java.util.*

internal fun parseRoute(route: String): String {
    val index = route.indexOfFirst { it == '?' }
    return if (index > 0) {
        route.substring(0, index)
    } else {
        route
    }
}

internal fun parseRouteParams(route: String): Array<Pair<String, String?>> {
    val uri = route.toUri()
    val query = uri.query

    if (query != null) {
        val result = mutableListOf<Pair<String, String?>>()
        uri.queryParameterNames.forEach {
            val value = uri.getQueryParameter(it)
            result.add(it to value)
        }
        return result.toTypedArray()
    }
    return arrayOf()
}

internal fun createDestinationDataTag(): String {
    return UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault())
}

internal fun Activity.key(): String {
    return "${javaClass.canonicalName}@${hashCode()}"
}