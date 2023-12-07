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
                    Butterfly.agile(Destinations.COMPOSE_HOME)
                        .container(R.id.container)
                        .group()
                        .carry(this)
                }
                R.id.navigation_dashboard -> {
                    Butterfly.agile(Destinations.COMPOSE_DASHBOARD)
                        .container(R.id.container)
                        .group()
                        .carry(this)
                }
                R.id.navigation_notifications -> {
                    Butterfly.agile(Destinations.COMPOSE_NOTIFICATION)
                        .container(R.id.container)
                        .group()
                        .carry(this)
                }
            }
            true
        }
        binding.navView.selectedItemId = R.id.navigation_home
    }
}