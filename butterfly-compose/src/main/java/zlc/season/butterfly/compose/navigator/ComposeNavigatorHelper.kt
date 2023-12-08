package zlc.season.butterfly.compose.navigator

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import zlc.season.butterfly.compose.ComposeDestination
import zlc.season.butterfly.compose.ComposeViewModel
import zlc.season.butterfly.compose.DestinationViewModelStoreOwner
import zlc.season.butterfly.compose.Utils
import zlc.season.butterfly.compose.Utils.findContainerView
import zlc.season.butterfly.entities.DestinationData

object ComposeNavigatorHelper {

    fun clearComposeView(activity: ComponentActivity, data: DestinationData) {
        val containerView = activity.findContainerView(data)
        val composeView = containerView.findViewWithTag<ComposeView>(Utils.COMPOSE_VIEW_TAG)
        if (composeView != null) {
            val composeViewModel = ComposeViewModel.getInstance(activity.viewModelStore)
            val destinationDataFlow = composeViewModel.getDestinationDataFlow(composeView.id)
            destinationDataFlow.value = null
        }
    }

    fun navigate(activity: ComponentActivity, data: DestinationData) {
        val containerView = activity.findContainerView(data)
        var composeView = containerView.findViewWithTag<ComposeView>(Utils.COMPOSE_VIEW_TAG)
        if (composeView == null) {
            composeView = createComposeView(containerView)

            val composeViewModel = ComposeViewModel.getInstance(activity.viewModelStore)
            val destinationDataFlow = composeViewModel.getDestinationDataFlow(composeView.id)
            destinationDataFlow.value = data

            composeView.setContent {
                val destinationData by destinationDataFlow.collectAsState()
                destinationData?.let {
                    val viewModelStoreOwner = DestinationViewModelStoreOwner(composeViewModel, it)

                    val composeDestination = createComposeDestination(it)

                    CompositionLocalProvider(LocalViewModelStoreOwner provides viewModelStoreOwner) {
                        when {
                            composeDestination.paramsViewModelComposable != null -> {
                                val viewModel = viewModelStoreOwner.getViewModel(composeDestination)
                                composeDestination.paramsViewModelComposable?.invoke(
                                    data.bundle,
                                    viewModel
                                )
                            }

                            composeDestination.viewModelComposable != null -> {
                                val viewModel = viewModelStoreOwner.getViewModel(composeDestination)
                                composeDestination.viewModelComposable?.invoke(viewModel)
                            }

                            composeDestination.paramsComposable != null -> {
                                composeDestination.paramsComposable?.invoke(data.bundle)
                            }

                            else -> {
                                composeDestination.composable?.invoke()
                            }
                        }
                    }
                }
            }
        } else {
            val composeViewModel = ComposeViewModel.getInstance(activity.viewModelStore)
            val destinationDataFlow = composeViewModel.getDestinationDataFlow(composeView.id)
            destinationDataFlow.value = data
        }
    }

    private fun createComposeView(containerView: ViewGroup): ComposeView {
        val composeView = ComposeView(containerView.context).apply {
            id = View.generateViewId()
            tag = Utils.COMPOSE_VIEW_TAG
        }
        containerView.addView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        return composeView
    }

    private fun createComposeDestination(data: DestinationData): ComposeDestination {
        return Class.forName(data.className)
            .getDeclaredConstructor()
            .newInstance() as ComposeDestination
    }
}