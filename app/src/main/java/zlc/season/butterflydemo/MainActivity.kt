package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.Butterfly.with
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.startActivity.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO)
                .with("intValue" to 1)
                .with("booleanValue" to true)
                .with("stringValue" to "test value")
                .carry()
        }

        binding.startActivityForResult.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FOO_RESULT)
                .with("intValue" to 1)
                .with("booleanValue" to true)
                .with("stringValue" to "test value")
                .carry {
                    if (it.isSuccess) {
                        val data = it.getOrThrow()
                        val result = data.getStringExtra("result")
                        binding.tvResult.text = result
                    }
                }
        }

        binding.startAction.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_ACTION).carry()
        }

        binding.showFragment.setOnClickListener {
            val home = Butterfly.evade<Home>()
            home.showHome(supportFragmentManager, R.id.container)
        }
    }

    override fun onBackPressed() {
        val home = Butterfly.evade<Home>()
        if (home.isHomeShowing(supportFragmentManager)) {
            home.hideHome(supportFragmentManager)
            return
        }
        super.onBackPressed()
    }
}