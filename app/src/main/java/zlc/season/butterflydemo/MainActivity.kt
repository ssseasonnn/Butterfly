package zlc.season.butterflydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zlc.season.base.Scheme
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.evade
import zlc.season.butterfly.Butterfly.evaded
import zlc.season.butterflydemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnButton.setOnClickListener {
            lifecycleScope.launch {
                val result = Butterfly.agile(Scheme.test).evaded(this@MainActivity)
                result.data?.let {
                    val result = it.getStringExtra("result")
                    println(result)
                }
            }
        }
    }
}