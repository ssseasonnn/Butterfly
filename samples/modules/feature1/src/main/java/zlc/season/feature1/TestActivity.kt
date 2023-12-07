package zlc.season.feature1

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.ActivityTestBinding

@Destination(Destinations.TEST)
class TestActivity : AppCompatActivity() {
    val intValue by params<Int>()
    val booleanValue by params<Boolean>()
    val stringValue by params<String>()

    val a by params<String>()
    val b by params<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvParam.text = """
            intValue = $intValue 
            booleanValue = $booleanValue
            stringValue = $stringValue
            a = $a
            b = $b
        """.trimIndent()

        binding.btnFinish.setOnClickListener {
            Butterfly.retreat("result" to "asb")
        }
    }
}