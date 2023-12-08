package zlc.season.feature1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import zlc.season.base.Destinations
import zlc.season.base.Destinations.DIALOG_FRAGMbENT
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.DialogTestBinding


@Destination(DIALOG_FRAGMbENT)
class TestDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.ThemeOverlay_Material_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return DialogTestBinding.inflate(inflater, container, false).also {
            it.btnNext.setOnClickListener {
                Butterfly.of(requireContext()).navigate(Destinations.FRAGMENT)
                dismiss()
            }
            it.btnBack.setOnClickListener {
                Butterfly.of(requireContext()).popBack("result" to "Result from dialog!")
            }
        }.root
    }
}