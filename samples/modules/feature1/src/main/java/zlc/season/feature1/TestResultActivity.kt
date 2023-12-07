package zlc.season.feature1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.annotation.Destination
import zlc.season.feature1.databinding.ActivityTestResultBinding

@Destination(Destinations.TEST_RESULT)
class TestResultActivity : AppCompatActivity() {
    val intValue by params<Int>()
    val booleanValue by params<Boolean>()
    val stringValue by params<String>()
    val a by params<String>()
    val b by params<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvParam.text = """
            intValue = $intValue 
            booleanValue = $booleanValue
            stringValue = $stringValue
            a = $a
            b = $b
        """.trimIndent()

        binding.btnFinish.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtras(
                    bundleOf(
                        "result" to "This is result"
                    )
                )
            })
            finish()

            // or use
            // Butterfly.retreat("result" to "aaa")
        }
    }
}