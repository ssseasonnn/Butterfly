package zlc.season.home

import android.content.Context
import android.os.Bundle
import zlc.season.butterfly.ButterflyHomeScheme
import zlc.season.butterfly.Action
import zlc.season.butterfly.annotation.Agile

@Agile(ButterflyHomeScheme.SCHEME_TEST_SERVICE)
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle?): Any {
        println("test service started")
        return Unit
    }
}