package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.startDestinationTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.DESTINATION_TEST)
        }
        binding.btnFragmentTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.FRAGMENT_DEMO)
        }
        binding.btnBottomNavigationTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.FRAGMENT_BOTTOM_NAVIGATION)
        }
        binding.btnComposeTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.COMPOSE_DEMO)
        }
        binding.btnComposeBottomNavigationTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.COMPOSE_BOTTOM_NAVIGATION)
        }
        binding.startEvadeTest.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.EVADE_TEST)
        }
    }
}