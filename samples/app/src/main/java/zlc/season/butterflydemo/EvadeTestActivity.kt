package zlc.season.butterflydemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.butterflydemo.databinding.ActivityEvadeTestBinding

@Agile(Schemes.SCHEME_EVADE_TEST)
class EvadeTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEvadeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val home = Butterfly.evade<Home>()

        binding.showFragment.setOnClickListener {
            home.showHome(supportFragmentManager, R.id.container)
        }

        binding.testCompose.setOnClickListener {
            setContent {
                home.testCompose().composable?.invoke()
            }
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