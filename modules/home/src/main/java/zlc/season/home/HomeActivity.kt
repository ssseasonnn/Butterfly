package zlc.season.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.carry
import zlc.season.home.databinding.ActivityHomeBinding
import java.lang.reflect.GenericArrayType

class HomeActivity : AppCompatActivity() {
    val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Butterfly.evade<Home>().showHome(supportFragmentManager, R.id.container)

        binding.bottomBar.onHomeClick = {
            Butterfly.evade<Home>().showHome(supportFragmentManager, R.id.container)
        }
        binding.bottomBar.onCartClick = {
            Butterfly.evade<Cart>().showCart(supportFragmentManager, R.id.container)
        }
        binding.bottomBar.onUserClick = {
            Butterfly.evade<User>().showUser(supportFragmentManager, R.id.container)
        }
    }
}