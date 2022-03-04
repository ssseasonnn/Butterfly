package zlc.season.butterfly.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Agile(
    val scheme: String
)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Evade(
    val identity: String = ""
)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class EvadeImpl(
    val singleton: Boolean = true,
    val identity: String = ""
)