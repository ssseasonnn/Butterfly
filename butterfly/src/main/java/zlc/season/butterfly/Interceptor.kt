package zlc.season.butterfly

import android.content.Context

interface Interceptor {
    suspend fun intercept(context: Context, request: Request): Request
}