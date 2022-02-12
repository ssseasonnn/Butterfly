package zlc.season.butterflydemo

import android.app.Application
import zlc.season.butterfly.ButterflyCore
import zlc.season.butterfly.ButterflyFooModule
import zlc.season.butterfly.ButterflyHomeModule

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ButterflyCore.init(ButterflyHomeModule(), ButterflyFooModule())
    }
}