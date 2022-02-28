package zlc.season.foo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import zlc.season.base.Schemes
import zlc.season.butterfly.Action
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_ACTION)
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}