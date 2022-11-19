package zlc.season.compose.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asFlow
import zlc.season.base.Schemes
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_COMPOSE_NOTIFICATION)
@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel) {
    val textFromViewModel = viewModel.text.asFlow().collectAsState(initial = "")

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "This is notification screen!"
            )
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 200.dp),
                text = textFromViewModel.value
            )
        }
    }
}