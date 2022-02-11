package zlc.season.home

import android.content.Context
import zlc.season.base.Scheme
import zlc.season.butterfly.ButterflyRequest
import zlc.season.butterfly.Service
import zlc.season.butterfly.annotation.Agile

@Agile(Scheme.service)
class TestService : Service {
    override suspend fun start(context: Context, butterflyRequest: ButterflyRequest): zlc.season.butterfly.Result {
        println("test service started")
        return zlc.season.butterfly.Result()
    }
}