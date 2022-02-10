package zlc.season.butterfly

import android.content.Context

interface Service {
    suspend fun start(context: Context, request: Request): Result
}