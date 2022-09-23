package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zlc.season.base.Schemes.SCHEME_FOO_FRAGMENT
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.popBack
import zlc.season.butterfly.Butterfly.popBackWithResult
import zlc.season.butterfly.Butterfly.setResult
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.FragmentFooBinding

@Agile(SCHEME_FOO_FRAGMENT)
class FooFragment : Fragment() {
    var binding: FragmentFooBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentFooBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            btnSetResult.setOnClickListener {
                popBackWithResult("abc" to "123")
            }
        }
    }
}