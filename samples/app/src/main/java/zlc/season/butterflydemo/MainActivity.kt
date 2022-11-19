package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import zlc.season.base.Schemes
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

        binding.startAgileTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_AGILE_TEST).carry()
        }
        binding.btnFragmentTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_DEMO).carry()
        }
        binding.btnBottomNavigationTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_FRAGMENT_BOTTOM_NAVIGATION).carry()
        }
        binding.btnComposeTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_COMPOSE_DEMO).carry()
        }
        binding.btnComposeBottomNavigationTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_COMPOSE_BOTTOM_NAVIGATION).carry()
        }
        binding.startEvadeTest.setOnClickListener {
            Butterfly.agile(Schemes.SCHEME_EVADE_TEST).carry()
        }
    }

    class TestInterceptor : ButterflyInterceptor {
        override suspend fun shouldIntercept(request: AgileRequest): Boolean {
            return true
        }

        override suspend fun intercept(request: AgileRequest) {
            println("intercepting")
            delay(1000)
            println("intercept finish")
        }
    }
}