package zlc.season.butterfly.module

import zlc.season.butterfly.annotation.EvadeData

interface Module {
    fun getAgile(): Map<String, Class<*>>
    fun getEvade(): Map<String, Class<*>>
    fun getEvadeImpl(): Map<String, EvadeData>
}