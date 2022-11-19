package zlc.season.bar

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.bundleOf
import zlc.season.base.Schemes
import zlc.season.bracer.params
import zlc.season.butterfly.Butterfly
import zlc.season.butterfly.annotation.Agile

@Preview
@Agile(Schemes.SCHEME_COMPOSE_A)
@Composable
fun ComposeScreenA(bundle: Bundle = bundleOf()) {
    val id by bundle.params<Int>()

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Colors.YELLOW
    ) {
        Box {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "This is ComposeScreen A ${id}")
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
                        .params(bundleOf("id" to id + 1))
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