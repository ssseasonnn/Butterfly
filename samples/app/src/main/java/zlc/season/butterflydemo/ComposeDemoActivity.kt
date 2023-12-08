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
            Butterfly.of(this).asRoot().navigate(Destinations.COMPOSE_A)
        }

        binding.btnStartB.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.COMPOSE_B)
        }
        binding.btnStartC.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.COMPOSE_C)
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        Butterfly.of(this).popBack("result" to "Result from Activity")
    }
}

