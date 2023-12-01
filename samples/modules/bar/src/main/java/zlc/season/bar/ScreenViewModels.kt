package zlc.season.bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AScreenViewModel : ViewModel() {

    val text = MutableStateFlow("")

    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                text.value = "This is count: ${count++} from A screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("A screen view model is cleared!")
    }
}

class BScreenViewModel : ViewModel() {

    val text = MutableStateFlow("")
    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                text.value = "This is count: ${count++} from B screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("B screen view model is cleared!")
    }
}

class CScreenViewModel : ViewModel() {

    val text = MutableStateFlow("")
    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                text.value = "This is count: ${count++} from C screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("C screen view model is cleared!")
    }
}