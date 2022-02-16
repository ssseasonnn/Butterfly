package zlc.season.home

import kotlinx.coroutines.CoroutineScope
import zlc.season.yasha.YashaDataSource
import zlc.season.yasha.YashaItem

class HomeDataSource(coroutineScope: CoroutineScope) : YashaDataSource(coroutineScope) {
    override suspend fun loadInitial(): List<YashaItem>? {
        return super.loadInitial()
    }
}

val data = """
    
""".trimIndent()