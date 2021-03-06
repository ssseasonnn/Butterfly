package zlc.season.bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.bar.databinding.ActivityAgileTestBinding
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.Butterfly.params
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_AGILE_TEST)
class AgileTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAgileTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startActivity.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO + "?a=1&b=2")
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .carry()
        }

        binding.startActivityForResult.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_RESULT + "?a=1&b=2")
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .carry {
                    val result = it.getStringExtra("result")
                    binding.tvResult.text = result
                }
        }

        binding.startAction.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_ACTION + "?a=1&b=2").carry()
        }
    }
}