package zlc.season.bar

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import zlc.season.bar.databinding.ActivityBottomTabBinding
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.butterfly.Butterfly.container
import zlc.season.butterfly.Butterfly.group
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_BOTTOM_TAB_TEST)
class BottomTabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomTabBinding
    private val GROUP_NAME = "main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBottomTabBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Butterfly.agile(Schemes.SCHEME_HOME)
                        .container(R.id.container)
                        .group(GROUP_NAME)
                        .carry()
                }
                R.id.navigation_dashboard -> {
                    Butterfly.agile(Schemes.SCHEME_DASHBOARD)
                        .container(R.id.container)
                        .group(GROUP_NAME)
                        .carry()
                }
                R.id.navigation_notifications -> {
                    Butterfly.agile(Schemes.SCHEME_NOTIFICATION)
                        .container(R.id.container)
                        .group(GROUP_NAME)
                        .carry()
                }
            }
            true
        }
        binding.navView.selectedItemId = R.id.navigation_home

    }
}