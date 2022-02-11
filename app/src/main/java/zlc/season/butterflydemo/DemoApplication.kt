package zlc.season.butterflydemo

import android.app.Application
import zlc.season.bufferfly.ButterflyModuleFoo
import zlc.season.bufferfly.ButterflyModuleHome
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.ButterflyCore

class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ButterflyCore.init(ButterflyModuleHome(), ButterflyModuleFoo())
    }
}