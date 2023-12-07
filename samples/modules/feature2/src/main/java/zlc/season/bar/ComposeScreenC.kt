package zlc.season.bar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import zlc.season.base.Destinations
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Destination

@Destination(Destinations.COMPOSE_C)
@Composable
fun ComposeScreenC() {
    val viewModel = viewModel<CScreenViewModel>()
    val textFromViewModel = viewModel.text.collectAsState(initial = "")
    val ctx = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize(), color = Colors.GREEN) {
        Box {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This is ComposeScreen C")
                Text(text = textFromViewModel.value)
                Button(onClick = {
                    Butterfly.retreat()
                }) {
                    Text(text = "Back")
                }
                Button(onClick = {
                    Butterfly.agile(Destinations.BOTTOM_SHEET_DIALOG_FRAGMENT).carry(ctx)
                }) {
                    Text(text = "Show Dialog")
                }

                var singleTop by remember { mutableStateOf(false) }
                var clearTop by remember { mutableStateOf(false) }

                Button(onClick = {
                    Butterfly.agile(Destinations.COMPOSE_A)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry(ctx)
                }) {
                    Text(text = "Next To A")
                }
                Button(onClick = {
                    Butterfly.agile(Destinations.COMPOSE_B)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry(ctx)
                }) {
                    Text(text = "Next To B")
                }
                Button(onClick = {
                    Butterfly.agile(Destinations.COMPOSE_C)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry(ctx)
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