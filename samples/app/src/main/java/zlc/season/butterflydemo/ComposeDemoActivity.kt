package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterflydemo.databinding.ActivityComposeDemoBinding


@Agile(Schemes.SCHEME_COMPOSE_DEMO)
class ComposeDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityComposeDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_COMPOSE_A).asRoot().carry()
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_COMPOSE_B).carry()
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_COMPOSE_C).carry()
        }
    }

    override fun onBackPressed() {
        Butterfly.retreat("result" to "Result from Activity")
    }
}

