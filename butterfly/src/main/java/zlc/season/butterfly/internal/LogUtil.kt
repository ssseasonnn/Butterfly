package zlc.season.butterfly.internal

import android.util.Log

internal var enableLog = true

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
