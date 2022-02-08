package zlc.season.butterflydemo

import android.app.Application
import zlc.season.bufferfly.ButterflyModuleApp
import zlc.season.butterfly.Butterfly

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Butterfly.init(ButterflyModuleApp())
    }
}