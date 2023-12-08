package zlc.season.butterfly.internal

import android.util.Log

var enableLog = true
val TAG = "Butterfly"

fun <T> T.logd(tag: String = ""): T {
    if (enableLog) {
        val realTag = tag.ifEmpty { TAG }
        if (this is Throwable) {
            Log.d(realTag, this.message ?: "", this)
        } else {
            Log.d(realTag, this.toString())
        }
    }
    return this
}

fun <T> T.logw(tag: String = ""): T {
    if (enableLog) {
        val realTag = tag.ifEmpty { TAG }
        if (this is Throwable) {
            Log.w(realTag, this.message ?: "", this)
        } else {
            Log.w(realTag, this.toString())
        }
    }
    return this
}
