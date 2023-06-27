package zlc.season.butterfly.compose

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import zlc.season.butterfly.AgileRequest

class ComposeViewModelStoreOwner(
    private val composeViewModel: ComposeViewModel,
    private val agileRequest: AgileRequest
) : ViewModelStoreOwner {
    override fun getViewModelStore(): ViewModelStore {
        return composeViewModel.getViewModelStore(agileRequest.uniqueTag)
    }
}