package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import zlc.season.base.Schemes
import zlc.season.base.Schemes.SCHEME_DIALOG_FRAGMbENT
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.DialogFooBinding


@Agile(SCHEME_DIALOG_FRAGMbENT)
class FooDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogFooBinding.inflate(inflater, container, false).also {
            it.btnNext.setOnClickListener {
                Butterfly.agile(Schemes.SCHEME_FRAGMENT).carry()
                dismiss()
            }
            it.btnBack.setOnClickListener {
                retreat("result" to "Result from dialog!")
            }
        }.root
    }
}