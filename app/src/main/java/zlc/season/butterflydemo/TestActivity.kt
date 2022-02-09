package zlc.season.butterflydemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zlc.season.butterfly.annotation.Agile

@Agile("test")
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }
}