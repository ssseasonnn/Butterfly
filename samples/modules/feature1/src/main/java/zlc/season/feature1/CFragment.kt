package zlc.season.feature1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zlc.season.base.BaseFragment
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
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
            Butterfly.of(requireContext()).popBack("result" to "Result from FragmentC")
        }

        btnDialog.setOnClickListener {
            Butterfly.of(requireContext())
                .navigate(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        tvResult.text = result
                    }
                }
        }
        btnNextA.setOnClickListener {
            Butterfly.of(requireContext())
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .navigate(Destinations.FRAGMENT_A) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        tvResult.text = result
                    }
                }
        }
        btnNextB.setOnClickListener {
            Butterfly.of(requireContext())
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .navigate(Destinations.FRAGMENT_B) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        tvResult.text = result
                    }
                }
        }
        btnNextC.setOnClickListener {
            Butterfly.of(requireContext())
                .run {
                    if (cbClearTop.isChecked) {
                        clearTop()
                    } else if (cbSingleTop.isChecked) {
                        singleTop()
                    } else {
                        this
                    }
                }
                .navigate(Destinations.FRAGMENT_C) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        tvResult.text = result
                    }
                }
        }
    }
}
