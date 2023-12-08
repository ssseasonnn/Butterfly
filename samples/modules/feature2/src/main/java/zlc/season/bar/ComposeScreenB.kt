package zlc.season.bar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination

@Destination(Destinations.COMPOSE_B)
@Composable
fun ComposeScreenB() {
    val ctx = LocalContext.current
    val viewModel = viewModel<BScreenViewModel>()
    val textFromViewModel = viewModel.text.collectAsState(initial = "")

    Surface(modifier = Modifier.fillMaxSize(), color = Colors.PURPLE) {
        Box {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This is ComposeScreen B")
                Text(text = textFromViewModel.value)
                Button(onClick = {
                    Butterfly.of(ctx).popBack()
                }) {
                    Text(text = "Back")
                }
                Button(onClick = {
                    Butterfly.of(ctx).navigate(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT)
                }) {
                    Text(text = "Show Dialog")
                }

                var singleTop by remember { mutableStateOf(false) }
                var clearTop by remember { mutableStateOf(false) }

                Button(onClick = {
                    Butterfly.of(ctx)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .navigate(Destinations.COMPOSE_A)
                }) {
                    Text(text = "Next To A")
                }
                Button(onClick = {
                    Butterfly.of(ctx)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .navigate(Destinations.COMPOSE_B)
                }) {
                    Text(text = "Next To B")
                }
                Button(onClick = {
                    Butterfly.of(ctx)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .navigate(Destinations.COMPOSE_C)
                }) {
                    Text(text = "Next to C")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = singleTop, onCheckedChange = { singleTop = it })
                    Text(text = "SingleTop")
                    Checkbox(checked = clearTop, onCheckedChange = { clearTop = it })
                    Text(text = "ClearTop")
                }

                Text(text = "result")
            }
        }
    }
}