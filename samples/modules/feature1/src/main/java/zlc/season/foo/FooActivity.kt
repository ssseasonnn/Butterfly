package zlc.season.foo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.ActivityFooBinding

@Agile(Schemes.SCHEME_FOO)
class FooActivity : AppCompatActivity() {
    val intValue by params<Int>()
    val booleanValue by params<Boolean>()
    val stringValue by params<String>()

    val a by params<String>()
    val b by params<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFooBinding.inflate(layoutInflater)
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