package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import zlc.season.base.Schemes
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_FOO_FRAGMENT_A)
class AFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvContent = view.findViewById<TextView>(R.id.tvContent)
        val btnBack = view.findViewById<Button>(R.id.btnSetResult)

        tvContent.text = "This is Fragment A ${hashCode()}"
        btnBack.setOnClickListener {

        }
    }
}