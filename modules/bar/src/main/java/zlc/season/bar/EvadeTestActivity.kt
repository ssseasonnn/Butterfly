package zlc.season.bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.bar.databinding.ActivityEvadeTestBinding
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_EVADE_TEST)
class EvadeTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEvadeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.showFragment.setOnClickListener {
            val home = Butterfly.evade<Home>()
            home.showHome(supportFragmentManager, R.id.container)
        }
    }

    override fun onBackPressed() {
        val home = Butterfly.evade<Home>()
        if (home.isHomeShowing(supportFragmentManager)) {
            home.hideHome(supportFragmentManager)
            return
        }
        super.onBackPressed()
    }
}