package zlc.season.home

import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.Action

class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle?): Any {
        println("test service started")
        return Unit
    }
}