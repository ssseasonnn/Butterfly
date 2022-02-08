package zlc.season.butterfly.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Butterfly(
    val scheme: String
)