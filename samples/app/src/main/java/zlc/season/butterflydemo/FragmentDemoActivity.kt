package zlc.season.butterflydemo

import android.os.Bundle
import zlc.season.base.BaseActivity
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterflydemo.databinding.ActivityFragmentDemoBinding

@Agile(Schemes.SCHEME_FRAGMENT_DEMO)
class FragmentDemoActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFragmentDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_A)
                .asRoot()
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_B)
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_C)
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
    }

    override fun onBackPressed() {
        Butterfly.retreat("result" to "Result from Activity")
    }
}