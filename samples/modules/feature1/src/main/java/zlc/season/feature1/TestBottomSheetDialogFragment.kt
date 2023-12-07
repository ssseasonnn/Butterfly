package zlc.season.feature1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import zlc.season.base.Destinations
import zlc.season.base.Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.DialogTestBinding

@Destination(BOTTOM_SHEET_DIALOG_FRAGMENT)
class TestBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogTestBinding.inflate(inflater, container, false).also {
            it.btnNext.setOnClickListener {
                Butterfly.agile(Destinations.FRAGMENT).carry(requireContext())
                retreat()
            }
            it.btnBack.setOnClickListener {
                retreat("result" to "Result from dialog!")
            }
        }.root
    }
}