package zlc.season.bar

import android.os.Bundle
import zlc.season.bar.databinding.ActivityFragmentTestBinding
import zlc.season.base.BaseActivity
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_FRAGMENT_TEST)
class FragmentTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFragmentTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_A)
                .carry {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_B)
                .carry {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_C)
                .carry {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }
    }

    override fun onBackPressed() {
        Butterfly.retreat("result" to "Result from Activity")
    }
}