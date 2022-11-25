package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterflydemo.databinding.ActivityFragmentBottomNavigationBinding

@Agile(Schemes.SCHEME_FRAGMENT_BOTTOM_NAVIGATION)
class FragmentBottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFragmentBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    Butterfly.agile(Schemes.SCHEME_HOME)
                        .container(R.id.container)
                        .group()
                        .carry(this)
                }
                R.id.navigation_dashboard -> {
                    Butterfly.agile(Schemes.SCHEME_DASHBOARD)
                        .container(R.id.container)
                        .group()
                        .carry(this)
                }
                R.id.navigation_notifications -> {
                    Butterfly.agile(Schemes.SCHEME_NOTIFICATION)
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