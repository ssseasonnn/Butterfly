package zlc.season.butterfly.annotation

interface Module {
    fun getAgile(): Map<String, String>
    fun getEvade(): Map<String, String>
    fun getEvadeImpl(): Map<String, EvadeData>
}