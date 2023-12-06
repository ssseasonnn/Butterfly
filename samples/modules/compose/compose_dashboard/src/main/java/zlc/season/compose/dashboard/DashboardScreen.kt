package zlc.season.compose.dashboard

import android.os.Bundle
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
import androidx.core.os.bundleOf
import zlc.season.base.Schemes
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_COMPOSE_DASHBOARD)
@Composable
fun DashboardScreen(test: Bundle = bundleOf(), viewModel: DashboardViewModel = DashboardViewModel()) {
    val textFromViewModel = viewModel.text.collectAsState(initial = "")

    Surface(modifier = Modifier.fillMaxSize()) {
        Box {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "This is dashboard screen!"
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