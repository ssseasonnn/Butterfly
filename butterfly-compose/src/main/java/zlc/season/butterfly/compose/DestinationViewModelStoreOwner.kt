package zlc.season.butterfly.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import zlc.season.butterfly.entities.DestinationData

class DestinationViewModelStoreOwner(
    private val composeViewModel: ComposeViewModel,
    private val destinationData: DestinationData
) : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore
        get() = composeViewModel.getViewModelStore(destinationData.uniqueTag)

    @Suppress("unchecked_cast")
    fun getViewModel(composable: ComposeDestination): ViewModel {
        val viewModelClass = Class.forName(composable.viewModelClass) as Class<ViewModel>
        return ViewModelProvider(this)[viewModelClass]
    }
}