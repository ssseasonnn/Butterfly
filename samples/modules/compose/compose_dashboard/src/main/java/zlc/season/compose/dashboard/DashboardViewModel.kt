package zlc.season.compose.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    val text = MutableStateFlow("")

    init {
        viewModelScope.launch {
            delay(1500)
            text.value = "This is text from dashboard viewModel"
        }
    }
}