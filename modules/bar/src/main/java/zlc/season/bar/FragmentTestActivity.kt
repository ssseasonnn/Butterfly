package zlc.season.bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.bar.databinding.ActivityFragmentTestBinding
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.Butterfly.clearTop
import zlc.season.butterfly.Butterfly.container
import zlc.season.butterfly.Butterfly.params
import zlc.season.butterfly.Butterfly.retreat
import zlc.season.butterfly.Butterfly.singleTop
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_FRAGMENT_TEST)
class FragmentTestActivity : AppCompatActivity() {
    var number = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFragmentTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_A)
                .container(R.id.container)
                .carry()
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_B)
                .container(R.id.container)
                .carry()
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_C)
                .container(R.id.container)
                .carry()
        }

        binding.btnClearTop.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_A)
                .container(R.id.container)
                .clearTop()
                .carry()
        }

        binding.btnSingleTop.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_FRAGMENT_C)
                .container(R.id.container)
                .singleTop()
                .carry()
        }

        binding.btnBack.setOnClickListener {
            Butterfly.retreatFragment()
        }
    }
}