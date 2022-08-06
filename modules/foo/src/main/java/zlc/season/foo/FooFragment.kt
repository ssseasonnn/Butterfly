package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import zlc.season.base.Schemes.SCHEME_FOO_FRAGMENT
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.FragmentFooBinding

@Agile(SCHEME_FOO_FRAGMENT)
class FooFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parentFragmentManager.setFragmentResult(javaClass.name, bundleOf("abc" to "123"))
        return FragmentFooBinding.inflate(inflater, container, false).root
    }


}