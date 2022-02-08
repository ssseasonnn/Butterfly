package zlc.season.butterfly

import zlc.season.butterfly.annotation.Module

object Butterfly {
    private val moduleController by lazy { ButterflyModuleController() }

    fun init(vararg module: Module) {
        moduleController.addModule(*module)
    }

    fun scheme(scheme: String): ButterflyRequest {
        val dest = moduleController.query(scheme)
        return if (dest.isNotEmpty()) {
            ButterflyRequest(scheme, dest)
        } else {
            ButterflyRequest.EMPTY
        }
    }

    fun ButterflyRequest.evade() {
        realEvade()
    }
}