package zlc.season.butterflydemo

import android.annotation.SuppressLint
import android.os.Bundle
import zlc.season.base.BaseActivity
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterflydemo.databinding.ActivityFragmentDemoBinding

@Destination(Destinations.FRAGMENT_DEMO)
class FragmentDemoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFragmentDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.of(this)
                .asRoot()
                .navigate(Destinations.FRAGMENT_A) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        binding.tvResult.text = result
                    }
                }
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.of(this)
                .navigate(Destinations.FRAGMENT_B) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        binding.tvResult.text = result
                    }
                }
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.of(this)
                .navigate(Destinations.FRAGMENT_C) {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        binding.tvResult.text = result
                    }
                }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Butterfly.of(this).popBack("result" to "Result from Activity")
    }
}