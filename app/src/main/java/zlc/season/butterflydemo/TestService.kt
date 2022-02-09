package zlc.season.butterflydemo

import android.content.Context
import zlc.season.butterfly.Request
import zlc.season.butterfly.Service
import zlc.season.butterfly.annotation.Agile

@Agile("test_service")
class TestService : Service {
    override suspend fun start(context: Context, request: Request) {
        println("test service started")
    }
}