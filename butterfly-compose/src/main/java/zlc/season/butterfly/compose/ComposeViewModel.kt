package zlc.season.butterfly.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get

/**
 * 该ViewModel用于辅助Compose页面创建基于回退栈生命周期的ViewModel
 */
class ComposeViewModel : ViewModel() {
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    fun clear(requestTag: String) {
        val viewModelStore = viewModelStores.remove(requestTag)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        for (store in viewModelStores.values) {
            store.clear()
        }
        viewModelStores.clear()
    }

    fun getViewModelStore(requestTag: String): ViewModelStore {
        var viewModelStore = viewModelStores[requestTag]
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
            viewModelStores[requestTag] = viewModelStore
        }
        return viewModelStore
    }

    override fun toString(): String {
        val sb = StringBuilder("ComposeViewModel{")
        sb.append(Integer.toHexString(System.identityHashCode(this)))
        sb.append("} ViewModelStores (")
        val viewModelStoreIterator: Iterator<String> = viewModelStores.keys.iterator()
        while (viewModelStoreIterator.hasNext()) {
            sb.append(viewModelStoreIterator.next())
            if (viewModelStoreIterator.hasNext()) {
                sb.append(", ")
            }
        }
        sb.append(')')
        return sb.toString()
    }

    companion object {
        private val FACTORY: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ComposeViewModel() as T
            }
        }

        fun getInstance(viewModelStore: ViewModelStore): ComposeViewModel {
            return ViewModelProvider(viewModelStore, FACTORY).get()
        }
    }
}
