package zlc.season.feature1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zlc.season.base.Destinations.FRAGMENT
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.FragmentTestBinding

@Destination(FRAGMENT)
class TestFragment : Fragment() {
    val number by params<Int>()

    var binding: FragmentTestBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentTestBinding.inflate(inflater, container, false).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            tvContent.text = "This is Fragment $number"
            btnSetResult.setOnClickListener {
                Butterfly.of(requireContext()).popBack("abc" to "123")
            }
        }
    }
}