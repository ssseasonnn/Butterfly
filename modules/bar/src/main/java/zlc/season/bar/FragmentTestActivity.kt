package zlc.season.bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.bar.databinding.ActivityFragmentTestBinding
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_FRAGMENT_TEST)
class FragmentTestActivity : AppCompatActivity() {
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
        if (!Butterfly.retreatFragment("result" to "Result from Activity")) {
            super.onBackPressed()
        }
    }
}