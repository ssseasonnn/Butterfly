package zlc.season.base

object Schemes {
    private const val HOST = "butterfly_demo"

    const val SCHEME_AGILE_TEST = "agile/test"
    const val SCHEME_FRAGMENT_TEST = "fragment/test"
    const val SCHEME_EVADE_TEST = "evade/test"

    const val SCHEME_FOO = "${HOST}://foo"
    const val SCHEME_FOO_RESULT = "${HOST}://foo_result"

    const val SCHEME_FOO_FRAGMENT = "${HOST}://foo_fragment"

    const val SCHEME_FOO_FRAGMENT_A = "${HOST}://a_fragment"
    const val SCHEME_FOO_FRAGMENT_B = "${HOST}://b_fragment"
    const val SCHEME_FOO_FRAGMENT_C = "${HOST}://c_fragment"

    const val SCHEME_FOO_DIALOG_FRAGMENT = "${HOST}://foo_dialog_fragment"
    const val SCHEME_FOO_BOTTOM_SHEET_DIALOG_FRAGMENT = "${HOST}://foo_bottom_sheet_dialog_fragment"

    const val SCHEME_ACTION = "${HOST}://action"
}