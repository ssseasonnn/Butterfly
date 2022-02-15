package zlc.season.home

import android.content.Context
import zlc.season.butterfly.ButterflyHomeScheme
import zlc.season.butterfly.ButterflyRequest
import zlc.season.butterfly.Action
import zlc.season.butterfly.annotation.Agile

@Agile(ButterflyHomeScheme.SCHEME_TEST_SERVICE)
class TestAction : Action {
    override suspend fun doAction(context: Context, butterflyRequest: ButterflyRequest): zlc.season.butterfly.Result {
        println("test service started")
        return zlc.season.butterfly.Result()
    }
}