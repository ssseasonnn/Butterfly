package zlc.season.feature1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zlc.season.base.BaseFragment
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.FragmentCommonBinding

@Destination(Destinations.FRAGMENT_C)
class CFragment : BaseFragment() {
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
        btnDialog.setOnClickListener {
            Butterfly.agile(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT)
                .carry(requireContext()) {
                    val result by it.params<String>()
                    tvResult.text = result
                }
        }
        btnNextA.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT_A)
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
            Butterfly.agile(Destinations.FRAGMENT_B)
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
            Butterfly.agile(Destinations.FRAGMENT_C)
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
