package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.Butterfly.clearTop
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.Butterfly.singleTop
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.FragmentCommonBinding

@Agile(Schemes.SCHEME_FOO_FRAGMENT_C)
class CFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCommonBinding.inflate(inflater, container, false).also {
            it.setup()
        }.root
    }

    private fun FragmentCommonBinding.setup() {
        root.setBackgroundResource(R.color.yellow)
        tvContent.text = "Fragment C ${hashCode()}"
        btnBack.setOnClickListener {
            retreat("result" to "Result from C")
        }
        btnNextA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_A)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_B)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_C)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
    }
}
