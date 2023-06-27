package zlc.season.bar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AScreenViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _text.value = "This is count: ${count++} from A screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("A screen view model is cleared!")
    }
}

class BScreenViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _text.value = "This is count: ${count++} from B screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("B screen view model is cleared!")
    }
}

class CScreenViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text
    var count = 0

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _text.value = "This is count: ${count++} from C screen viewModel"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        println("C screen view model is cleared!")
    }
}