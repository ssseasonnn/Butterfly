package zlc.season.compose.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    val text = MutableStateFlow("")

    init {
        viewModelScope.launch {
            delay(1500)
            text.value = "This is text from notification viewModel"
        }
    }
}