package zlc.season.compose.home

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import zlc.season.base.Schemes
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_COMPOSE_HOME)
@Composable
fun HomeScreen(test: Bundle) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "This is home screen!"
            )
        }
    }
}