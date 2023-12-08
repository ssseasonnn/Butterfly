package zlc.season.butterfly.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.get
import kotlinx.coroutines.flow.MutableStateFlow
import zlc.season.butterfly.entities.DestinationData

/**
 * This ViewModel is used to help Compose Destination Page in creating a ViewModel
 * based on the fallback stack lifecycle.
 */
class ComposeViewModel : ViewModel() {
    /**
     * Save [ComposeViewId : Flow]
     */
    private val destinationDataFlows = mutableMapOf<Int, MutableStateFlow<DestinationData?>>()

    /**
     * Save [destinationDataTag : ViewModelStore]
     */
    private val viewModelStores = mutableMapOf<String, ViewModelStore>()

    fun getAllDestinationDataFlows(): List<MutableStateFlow<DestinationData?>> {
        return destinationDataFlows.values.toList()
    }

    fun getDestinationDataFlow(
        viewId: Int,
        data: DestinationData? = null
    ): MutableStateFlow<DestinationData?> {
        var flow = destinationDataFlows[viewId]
        if (flow == null) {
            flow = MutableStateFlow(data)
            destinationDataFlows[viewId] = flow
        }
        return flow
    }

    fun getViewModelStore(destinationDataTag: String): ViewModelStore {
        var viewModelStore = viewModelStores[destinationDataTag]
        if (viewModelStore == null) {
            viewModelStore = ViewModelStore()
            viewModelStores[destinationDataTag] = viewModelStore
        }
        return viewModelStore
    }

    fun clear(destinationDataTag: String) {
        val viewModelStore = viewModelStores.remove(destinationDataTag)
        viewModelStore?.clear()
    }

    override fun onCleared() {
        // clear flow
        destinationDataFlows.values.forEach {
            it.value = null
        }
        destinationDataFlows.clear()

        // clear viewModelStore
        viewModelStores.values.forEach {
            it.clear()
        }
        viewModelStores.clear()
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
