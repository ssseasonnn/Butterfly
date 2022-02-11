package zlc.season.butterfly.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Agile(
    val scheme: String
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Evade(
    val scheme: String
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class EvadeImpl(
    val scheme: String,
    val singleton: Boolean = false
)