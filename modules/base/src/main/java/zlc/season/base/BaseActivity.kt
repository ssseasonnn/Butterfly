package zlc.season.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    open val name: String = "$this"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("$name onCreate: $savedInstanceState")
    }

    override fun onRestart() {
        super.onRestart()
        println("$name onRestart")
    }


    override fun onDestroy() {
        super.onDestroy()
        println("$name onDestroy")
    }
}