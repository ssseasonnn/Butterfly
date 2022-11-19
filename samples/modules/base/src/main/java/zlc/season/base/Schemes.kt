package zlc.season.base

object Schemes {
    private const val HOST = "butterfly_demo"

    const val SCHEME_AGILE_TEST = "agile/test"
    const val SCHEME_EVADE_TEST = "evade/test"

    const val SCHEME_FRAGMENT_DEMO = "fragment/demo"
    const val SCHEME_FRAGMENT_BOTTOM_NAVIGATION = "fragment/bottom_navigation"
    const val SCHEME_COMPOSE_DEMO = "compose/demo"
    const val SCHEME_COMPOSE_BOTTOM_NAVIGATION = "compose/bottom_navigation"

    const val SCHEME_FOO = "${HOST}://foo"
    const val SCHEME_FOO_RESULT = "${HOST}://foo_result"
    const val SCHEME_FRAGMENT = "${HOST}://foo_fragment"
    const val SCHEME_DIALOG_FRAGMbENT = "${HOST}://foo_dialog_fragment"
    const val SCHEME_BOTTOM_SHEET_DIALOG_FRAGMENT = "${HOST}://foo_bottom_sheet_dialog_fragment"
    const val SCHEME_ACTION = "${HOST}://action"

    const val SCHEME_FRAGMENT_A = "${HOST}://a_fragment"
    const val SCHEME_FRAGMENT_B = "${HOST}://b_fragment"
    const val SCHEME_FRAGMENT_C = "${HOST}://c_fragment"

    const val SCHEME_COMPOSE_A = "${HOST}://a_screen"
    const val SCHEME_COMPOSE_B = "${HOST}://b_screen"
    const val SCHEME_COMPOSE_C = "${HOST}://c_screen"

    const val SCHEME_DASHBOARD = "${HOST}://dashboard"
    const val SCHEME_NOTIFICATION = "${HOST}://notification"
    const val SCHEME_HOME = "${HOST}://home"

    const val SCHEME_COMPOSE_DASHBOARD = "${HOST}://compose/dashboard"
    const val SCHEME_COMPOSE_NOTIFICATION = "${HOST}://compose/notification"
    const val SCHEME_COMPOSE_HOME = "${HOST}://compose/home"
}