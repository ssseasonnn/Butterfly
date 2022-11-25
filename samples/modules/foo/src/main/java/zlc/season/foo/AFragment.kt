package zlc.season.foo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import zlc.season.base.BaseFragment
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.FragmentCommonBinding

@Agile(Schemes.SCHEME_FRAGMENT_A)
class AFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentCommonBinding.inflate(inflater, container, false).also {
            it.setup()
        }.root
    }

    private fun FragmentCommonBinding.setup() {
        root.setBackgroundResource(R.color.blue)
        tvContent.text = "Fragment A ${hashCode()}"
        btnBack.setOnClickListener {
            retreat("result" to "Result from A")
        }
        btnDialog.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_BOTTOM_SHEET_DIALOG_FRAGMENT)
                .carry(requireContext()) {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_A)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry(requireContext()) {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_B)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry(requireContext()) {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_C)
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .carry(requireContext()) {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
    }
}