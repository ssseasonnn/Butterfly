package zlc.season.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import zlc.season.base.Scheme
import zlc.season.butterfly.SchemeTestSchemeConfig
import zlc.season.butterfly.annotation.Agile

@Agile(SchemeTestSchemeConfig.SCHEME_TEST)
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun finish() {
        val data = Intent()
        data.putExtra("result", "this is result")
        setResult(Activity.RESULT_OK, data)
        super.finish()
    }
}