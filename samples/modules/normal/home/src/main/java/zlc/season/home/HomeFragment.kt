package zlc.season.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import zlc.season.base.Destinations
import zlc.season.butterfly.annotation.Destination
import zlc.season.home.databinding.FragmentHomeBinding

@Destination(Destinations.HOME)
class HomeFragment : Fragment() {
    var binding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val textView: TextView = binding!!.textHome
        textView.text = "This is HomeFragment"
        return root
    }
}
