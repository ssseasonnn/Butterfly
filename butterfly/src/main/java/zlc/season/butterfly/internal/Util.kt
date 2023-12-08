package zlc.season.butterfly.internal

import android.app.Activity
import android.util.Log
import androidx.core.net.toUri
import java.util.*

internal fun parseRouteScheme(scheme: String): String {
    val index = scheme.indexOfFirst { it == '?' }
    return if (index > 0) {
        scheme.substring(0, index)
    } else {
        scheme
    }
}

internal fun parseRouteSchemeParams(scheme: String): Array<Pair<String, String?>> {
    val uri = scheme.toUri()
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