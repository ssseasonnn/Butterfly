package zlc.season.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        }
    }

    private fun changeTab(scheme: String) {
        val fragment = Butterfly.agile(scheme).carry() as Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, scheme)
            .commit()
    }
}