package zlc.season.butterflydemo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Destinations
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination
import zlc.season.butterflydemo.databinding.ActivityDestinationTestBinding

@Destination(Destinations.DESTINATION_TEST)
class DestinationTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDestinationTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startActivity.setOnClickListener {
            Butterfly.agile(Destinations.TEST + "?a=1&b=2")
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .carry(this)
        }

        binding.startActivityForResult.setOnClickListener {
            Butterfly.agile(Destinations.TEST_RESULT + "?a=1&b=2")
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .carry(this) {
                    val result by it.params<String>()
                    binding.tvResult.text = result
                }
        }

        binding.startAction.setOnClickListener {
            Butterfly.agile(Destinations.ACTION + "?a=1&b=2").carry(this)
        }

        binding.startFragment.setOnClickListener {
            Butterfly.agile(Destinations.FRAGMENT)
                .carry(this) {
                    val abc by it.params<String>()
                    binding.tvResult.text = abc
                }
        }

        binding.startDialogFragment.setOnClickListener {
            Butterfly.agile(Destinations.DIALOG_FRAGMbENT).carry(this)
        }

        binding.startBottomSheetDialogFragment.setOnClickListener {
            Butterfly.agile(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT).carry(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        super.onBackPressed()
        Butterfly.retreat()
    }
}