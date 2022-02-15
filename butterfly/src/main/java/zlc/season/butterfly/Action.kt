package zlc.season.butterfly

import android.content.Context

interface Action {
    fun doAction(context: Context, request: AgileRequest): Any
}