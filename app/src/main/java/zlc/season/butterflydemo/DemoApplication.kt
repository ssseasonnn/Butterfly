package zlc.season.butterflydemo

import android.app.Application
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.ButterflyCartModule
import zlc.season.butterfly.ButterflyFooModule
import zlc.season.butterfly.ButterflyHomeModule

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Butterfly.init(ButterflyHomeModule(), ButterflyCartModule(), ButterflyFooModule())
    }
}