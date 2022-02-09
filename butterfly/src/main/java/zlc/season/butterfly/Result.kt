package zlc.season.butterfly

import android.content.Intent

data class Result(val data: Intent? = null) {
    fun isSuccess(): Boolean {
        return data != null
    }
}