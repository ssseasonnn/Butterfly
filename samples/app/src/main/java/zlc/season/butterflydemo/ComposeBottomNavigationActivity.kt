package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterflydemo.databinding.ActivityComposeBottomNavigationBinding

@Destination(Destinations.COMPOSE_BOTTOM_NAVIGATION)
class ComposeBottomNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityComposeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group("")
                        .navigate(Destinations.COMPOSE_HOME)
                }

                R.id.navigation_dashboard -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group("")
                        .navigate(Destinations.COMPOSE_DASHBOARD)
                }

                R.id.navigation_notifications -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group("")
                        .navigate(Destinations.COMPOSE_NOTIFICATION)
                }
            }
            true
        }
        binding.navView.selectedItemId = R.id.navigation_home
    }
}