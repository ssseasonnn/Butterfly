package zlc.season.butterfly.compose.launcher

import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import zlc.season.butterfly.compose.ComposeDestination
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.compose.ComposeViewModelStoreOwner
import zlc.season.butterfly.compose.Utils.COMPOSE_VIEW_TAG
import zlc.season.butterfly.compose.Utils.findContainerView
import zlc.season.butterfly.entities.DestinationData

class ComposeBasicLauncher {
    fun ComponentActivity.launch(data: DestinationData) {
        val containerView = findContainerView(data)
        var composeView = containerView.findViewWithTag<ComposeView>(COMPOSE_VIEW_TAG)
        if (composeView == null) {
            composeView = ComposeView(this).apply { tag = COMPOSE_VIEW_TAG }
            containerView.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        invokeCompose(composeView, data)
    }

    private fun ComponentActivity.invokeCompose(
        composeView: ComposeView,
        data: DestinationData
    ) {
        val composable = Class.forName(data.className)
            .getDeclaredConstructor()
            .newInstance() as ComposeDestination
        composeView.setContent {
            val composeViewModel = ComposeViewModel.getInstance(viewModelStore)
            val composeViewModelStoreOwner = ComposeViewModelStoreOwner(composeViewModel, data)
            CompositionLocalProvider(LocalViewModelStoreOwner provides composeViewModelStoreOwner) {
                if (composable.paramsViewModelComposable != null) {
                    composable.paramsViewModelComposable?.invoke(
                        data.bundle,
                        getViewModel(composeViewModelStoreOwner, composable)
                    )
                } else if (composable.viewModelComposable != null) {
                    composable.viewModelComposable?.invoke(
                        getViewModel(composeViewModelStoreOwner, composable)
                    )
                } else if (composable.paramsComposable != null) {
                    composable.paramsComposable?.invoke(data.bundle)
                } else {
                    composable.composable?.invoke()
                }
            }
        }
    }

    @Suppress("unchecked_cast")
    private fun getViewModel(
        viewModelStoreOwner: ComposeViewModelStoreOwner,
        composable: ComposeDestination
    ): ViewModel {
        return ViewModelProvider(viewModelStoreOwner)[Class.forName(composable.viewModelClass) as Class<ViewModel>]
    }
}