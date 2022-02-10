package zlc.season.home

import android.content.Context
import zlc.season.butterfly.Request
import zlc.season.butterfly.Result
import zlc.season.butterfly.Service
import zlc.season.butterfly.annotation.Agile

@Agile("test_service")
class TestService : Service {
    override suspend fun start(context: Context, request: Request): Result {
        println("test service started")
        return Result()
    }
}