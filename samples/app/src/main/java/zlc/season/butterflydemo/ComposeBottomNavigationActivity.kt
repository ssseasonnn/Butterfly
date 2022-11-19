package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterflydemo.databinding.ActivityComposeBottomNavigationBinding

@Agile(Schemes.SCHEME_COMPOSE_BOTTOM_NAVIGATION)
class ComposeBottomNavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityComposeBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_HOME)
                        .container(R.id.container)
                        .group()
                        .carry()
                }
                R.id.navigation_dashboard -> {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_DASHBOARD)
                        .container(R.id.container)
                        .group()
                        .carry()
                }
                R.id.navigation_notifications -> {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_NOTIFICATION)
                        .container(R.id.container)
                        .group()
                        .carry()
                }
            }
            true
        }
        binding.navView.selectedItemId = R.id.navigation_home
    }
}