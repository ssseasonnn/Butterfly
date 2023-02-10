package zlc.season.butterfly.internal

import android.app.Activity
import android.util.Log
import androidx.core.net.toUri
import java.util.*

internal var enableLog = false

internal fun <T> T.logd(tag: String = ""): T {
    if (enableLog) {
        val realTag = tag.ifEmpty { "Butterfly" }
        if (this is Throwable) {
            Log.d(realTag, this.message ?: "", this)
        } else {
            Log.d(realTag, this.toString())
        }
    }
    return this
}

internal fun <T> T.logw(tag: String = ""): T {
    if (enableLog) {
        val realTag = tag.ifEmpty { "Butterfly" }
        if (this is Throwable) {
            Log.w(realTag, this.message ?: "", this)
        } else {
            Log.w(realTag, this.toString())
        }
    }
    return this
}


internal fun parseScheme(scheme: String): String {
    val index = scheme.indexOfFirst { it == '?' }
    return if (index > 0) {
        scheme.substring(0, index)
    } else {
        scheme
    }
}

internal fun parseSchemeParams(scheme: String): Array<Pair<String, String?>> {
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

internal fun createRequestTag(): String {
    return UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault())
}

internal fun Activity.key(): String {
    return "Activity@${hashCode()}"
}