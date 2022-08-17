package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import zlc.season.base.Schemes.SCHEME_FOO_DIALOG_FRAGMENT
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.FragmentFooBinding

@Agile(SCHEME_FOO_DIALOG_FRAGMENT)
class FooDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentFooBinding.inflate(inflater, container, false).root
    }
}