package zlc.season.foo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.annotation.Agile
import zlc.season.foo.databinding.ActivityFooBinding

@Agile(Schemes.SCHEME_FOO)
class FooActivity : AppCompatActivity() {
    val intValue by params<Int>()
    val booleanValue by params<Boolean>()
    val stringValue by params<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFooBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvParam.text = """
            intValue = $intValue 
            booleanValue = $booleanValue
            stringValue = $stringValue
        """.trimIndent()

        binding.btnFinish.setOnClickListener {
            finish()
        }
    }
}