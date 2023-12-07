package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterflydemo.databinding.ActivityComposeDemoBinding


@Destination(Destinations.COMPOSE_DEMO)
class ComposeDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityComposeDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStartA.setOnClickListener {
            Butterfly.agile(Destinations.COMPOSE_A).asRoot().carry(this)
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.agile(Destinations.COMPOSE_B).carry(this)
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.agile(Destinations.COMPOSE_C).carry(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Butterfly.retreat("result" to "Result from Activity")
    }
}

