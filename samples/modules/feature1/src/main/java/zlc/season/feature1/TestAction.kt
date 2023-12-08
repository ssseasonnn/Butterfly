package zlc.season.feature1

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import zlc.season.base.Destinations
import zlc.season.butterfly.action.Action
import zlc.season.butterfly.annotation.Destination

@Destination(Destinations.ACTION)
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}