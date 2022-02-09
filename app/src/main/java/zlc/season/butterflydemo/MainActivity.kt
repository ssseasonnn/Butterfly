package zlc.season.butterflydemo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.Butterfly.evade
import zlc.season.butterfly.annotation.Agile

@Agile("aaa")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btn_action)
        button.setOnClickListener {
            lifecycleScope.launch {
                Butterfly.agile("test").evade(this@MainActivity)
                Butterfly.agile("test_service").evade(this@MainActivity)
            }
        }
    }
}