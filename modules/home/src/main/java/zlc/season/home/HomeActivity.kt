package zlc.season.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.home.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        changeTab("home")

        binding.bottomBar.onHomeClick = {
            changeTab("home")
        }
        binding.bottomBar.onCartClick = {
            changeTab("cart")
        }
        binding.bottomBar.onUserClick = {
            changeTab("user")
        }
    }

    private fun changeTab(scheme: String) {
        val fragmentCreator = Butterfly.evade(scheme).carry() as FragmentCreator
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragmentCreator.create(), "tag")
            .commit()
    }
}