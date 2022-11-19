package zlc.season.bar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import zlc.season.base.Schemes
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile

@Agile(Schemes.SCHEME_COMPOSE_C)
@Composable
fun ComposeScreenC() {
    Surface(modifier = Modifier.fillMaxSize(), color = Colors.GREEN) {
        Box {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This is ComposeScreen C")
                Button(onClick = {
                    Butterfly.retreat()
                }) {
                    Text(text = "Back")
                }
                Button(onClick = {
                    Butterfly.agile(Schemes.SCHEME_BOTTOM_SHEET_DIALOG_FRAGMENT).carry()
                }) {
                    Text(text = "Show Dialog")
                }

                var singleTop by remember { mutableStateOf(false) }
                var clearTop by remember { mutableStateOf(false) }

                Button(onClick = {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_A)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry()
                }) {
                    Text(text = "Next To A")
                }
                Button(onClick = {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_B)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry()
                }) {
                    Text(text = "Next To B")
                }
                Button(onClick = {
                    Butterfly.agile(Schemes.SCHEME_COMPOSE_C)
                        .run {
                            if (clearTop) {
                                clearTop()
                            } else if (singleTop) {
                                singleTop()
                            } else {
                                this
                            }
                        }
                        .carry()
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