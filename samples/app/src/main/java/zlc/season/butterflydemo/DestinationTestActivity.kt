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
            Butterfly.of(this)
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .navigate(Destinations.TEST + "?a=1&b=2")
        }

        binding.startActivityForResult.setOnClickListener {
            Butterfly.of(this)
                .params(
                    "intValue" to 1,
                    "booleanValue" to true,
                    "stringValue" to "test value"
                )
                .navigate(Destinations.TEST_RESULT + "?a=1&b=2") {
                    if (it.isSuccess) {
                        val bundle = it.getOrDefault(Bundle.EMPTY)
                        val result by bundle.params<String>()
                        binding.tvResult.text = result
                    }
                }
        }

        binding.startAction.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.ACTION + "?a=1&b=2")
        }

        binding.startFragment.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.FRAGMENT) {
                if (it.isSuccess) {
                    val bundle = it.getOrDefault(Bundle.EMPTY)
                    val abc by bundle.params<String>()
                    binding.tvResult.text = abc
                }
            }
        }

        binding.startDialogFragment.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.DIALOG_FRAGMbENT)
        }

        binding.startBottomSheetDialogFragment.setOnClickListener {
            Butterfly.of(this).navigate(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
//        super.onBackPressed()
        Butterfly.of(this).popBack()
    }
}