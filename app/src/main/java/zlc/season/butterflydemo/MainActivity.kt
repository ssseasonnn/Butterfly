package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.startAgileTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_AGILE_TEST).carry()
        }

        binding.startEvadeTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_EVADE_TEST).carry()
        }
    }
}