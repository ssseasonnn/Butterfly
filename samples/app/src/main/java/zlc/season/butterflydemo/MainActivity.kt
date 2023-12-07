package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import zlc.season.base.Destinations
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.ButterflyInterceptor
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        ButterflyCore.addInterceptor(TestInterceptor())

        binding.startDestinationTest.setOnClickListener {
            Butterfly.agile(Destinations.DESTINATION_TEST).carry(this)
        }
        binding.btnFragmentTest.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT_DEMO).carry(this)
        }
        binding.btnBottomNavigationTest.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT_BOTTOM_NAVIGATION).carry(this)
        }
        binding.btnComposeTest.setOnClickListener {
            Butterfly.agile(Destinations.COMPOSE_DEMO).carry(this)
        }
        binding.btnComposeBottomNavigationTest.setOnClickListener {
            Butterfly.agile(Destinations.COMPOSE_BOTTOM_NAVIGATION).carry(this)
        }
        binding.startEvadeTest.setOnClickListener {
            Butterfly.agile(Destinations.EVADE_TEST).carry(this)
        }
    }

    class TestInterceptor : ButterflyInterceptor {
        override suspend fun shouldIntercept(request: AgileRequest): Boolean {
            return true
        }

        override suspend fun intercept(request: AgileRequest): AgileRequest {
            println("intercepting")
            delay(1000)
            println("intercept finish")
            return request
        }
    }
}