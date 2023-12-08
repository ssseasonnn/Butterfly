package zlc.season.butterfly.compose

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import zlc.season.butterfly.entities.DestinationData

class ComposeViewModelStoreOwner(
    private val composeViewModel: ComposeViewModel,
    private val destinationData: DestinationData
) : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore
        get() = composeViewModel.getViewModelStore(destinationData.uniqueTag)
}