package zlc.season.butterfly.compose.launcher

import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.compose.AgileComposable
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.compose.ComposeViewModelStoreOwner
import zlc.season.butterfly.compose.Utils.COMPOSE_VIEW_TAG
import zlc.season.butterfly.compose.Utils.findContainerView

class ComposeBasicLauncher {
    fun ComponentActivity.launch(request: AgileRequest) {
        val containerView = findContainerView(request)
        var composeView = containerView.findViewWithTag<ComposeView>(COMPOSE_VIEW_TAG)
        if (composeView == null) {
            composeView = ComposeView(this).apply { tag = COMPOSE_VIEW_TAG }
            containerView.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        invokeCompose(composeView, request)
    }

    private fun ComponentActivity.invokeCompose(composeView: ComposeView, request: AgileRequest) {
        val composable = Class.forName(request.className).newInstance() as AgileComposable
        composeView.setContent {
            val composeViewModel = ComposeViewModel.getInstance(viewModelStore)
            val composeViewModelStoreOwner = ComposeViewModelStoreOwner(composeViewModel, request)
            CompositionLocalProvider(LocalViewModelStoreOwner provides composeViewModelStoreOwner) {
                if (composable.paramsViewModelComposable != null) {
                    composable.paramsViewModelComposable?.invoke(
                        request.bundle,
                        getViewModel(composeViewModelStoreOwner, composable)
                    )
                } else if (composable.viewModelComposable != null) {
                    composable.viewModelComposable?.invoke(
                        getViewModel(composeViewModelStoreOwner, composable)
                    )
                } else if (composable.paramsComposable != null) {
                    composable.paramsComposable?.invoke(request.bundle)
                } else {
                    composable.composable?.invoke()
                }
            }
        }
    }

    @Suppress("unchecked_cast")
    private fun getViewModel(
        viewModelStoreOwner: ComposeViewModelStoreOwner,
        composable: AgileComposable
    ): ViewModel {
        return ViewModelProvider(viewModelStoreOwner)[Class.forName(composable.viewModelClass) as Class<ViewModel>]
    }
}