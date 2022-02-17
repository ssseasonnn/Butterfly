package zlc.season.butterflydemo

import android.app.Application
import zlc.season.butterfly.*

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ButterflyCore.init(ButterflyHomeModule(), ButterflyCartModule(), ButterflyUserModule())
    }
}