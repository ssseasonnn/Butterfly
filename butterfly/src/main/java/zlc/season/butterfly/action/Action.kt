package zlc.season.butterfly.action

import android.content.Context
import android.os.Bundle

interface Action {
    fun doAction(context: Context, scheme: String, data: Bundle = Bundle())
}