package zlc.season.butterfly.annotation

interface Module {
    fun getAgile(): Map<String, Class<*>>
    fun getEvade(): Map<String, Class<*>>
    fun getEvadeImpl(): Map<String, EvadeData>
}