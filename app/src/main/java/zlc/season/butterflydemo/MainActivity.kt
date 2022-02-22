package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.butterfly.Butterfly
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val home = Butterfly.evade<Home>()
        home.showHome(supportFragmentManager, R.id.container)

        binding.bottomBar.onHomeClick = {
            home.showHome(supportFragmentManager, R.id.container)
        }
        binding.bottomBar.onCartClick = {
            val cart = Butterfly.evade<Cart>()
            cart.showCart(supportFragmentManager, R.id.container)
        }
        binding.bottomBar.onUserClick = {
            val user = Butterfly.evade<User>()
            user.showUser(supportFragmentManager, R.id.container)
        }
    }
}