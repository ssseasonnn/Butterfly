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
            Butterfly.agile(Destinations.FRAGMENT_A)
                .asRoot()
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT_B)
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT_C)
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Butterfly.retreat("result" to "Result from Activity")
    }
}