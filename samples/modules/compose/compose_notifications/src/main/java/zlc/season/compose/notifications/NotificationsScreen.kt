package zlc.season.compose.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import zlc.season.base.Destinations
import zlc.season.butterfly.annotation.Destination

@Destination(Destinations.COMPOSE_NOTIFICATION)
@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel) {
    val textFromViewModel = viewModel.text.collectAsState(initial = "")

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