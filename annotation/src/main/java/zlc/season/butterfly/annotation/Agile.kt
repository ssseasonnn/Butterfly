package zlc.season.butterfly.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class Agile(
    val scheme: String
)