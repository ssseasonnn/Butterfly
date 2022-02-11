package zlc.season.foo

import zlc.season.butterfly.annotation.EvadeImpl

@EvadeImpl("path/evade_test")
class EvadeTestImpl {
    fun test() {
        println("evade test impl")
    }
}