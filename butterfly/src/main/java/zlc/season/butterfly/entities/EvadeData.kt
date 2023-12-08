package zlc.season.butterfly.entities

data class EvadeData(
    val identity: String,
    val className: String,
    val implClassName: String,
    val isSingleton: Boolean
) {
    override fun toString(): String {
        return """[identity="$identity", className="$className", implClassName="$implClassName", isSingleton="$isSingleton"]"""
    }
}
