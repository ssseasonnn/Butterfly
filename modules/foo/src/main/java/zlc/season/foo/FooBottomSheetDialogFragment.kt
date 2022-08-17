package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import zlc.season.base.Schemes.SCHEME_FOO_BOTTOM_SHEET_DIALOG_FRAGMENT
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.DialogFooBinding

@Agile(SCHEME_FOO_BOTTOM_SHEET_DIALOG_FRAGMENT)
class FooBottomSheetDialogFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return DialogFooBinding.inflate(inflater, container, false).root
    }
}