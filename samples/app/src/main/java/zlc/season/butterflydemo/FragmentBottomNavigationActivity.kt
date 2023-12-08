package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterflydemo.databinding.ActivityFragmentBottomNavigationBinding

@Destination(Destinations.FRAGMENT_BOTTOM_NAVIGATION)
class FragmentBottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFragmentBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val groupId = "test_group"
        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group(groupId)
                        .navigate(Destinations.HOME)
                }

                R.id.navigation_dashboard -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group(groupId)
                        .navigate(Destinations.DASHBOARD)
                }

                R.id.navigation_notifications -> {
                    Butterfly.of(this)
                        .container(R.id.container)
                        .group(groupId)
                        .navigate(Destinations.NOTIFICATION)
                }
            }
            true
        }
        binding.navView.selectedItemId = R.id.navigation_home

    }
}