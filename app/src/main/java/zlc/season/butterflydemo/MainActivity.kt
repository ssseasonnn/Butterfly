package zlc.season.butterflydemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zlc.season.butterfly.Butterfly
import zlc.season.butterflydemo.databinding.ActivityMainBinding
import zlc.season.home.EvadeTest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnButton.setOnClickListener {
            lifecycleScope.launch {
//                Butterfly.agile(Scheme.test).start()

//                result.data?.let {
//                    val result = it.getStringExtra("result")
//                    println(result)
//                }

                val evadeTest = Butterfly.evade("path/evade_test").get() as EvadeTest
                evadeTest?.test()
            }
        }
    }
}