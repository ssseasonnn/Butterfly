package zlc.season.butterfly.compose.launcher

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import zlc.season.butterfly.AgileRequest
import zlc.season.butterfly.compose.AgileComposable
import zlc.season.butterfly.compose.Utils.composeViewId
import zlc.season.butterfly.internal.ButterflyHelper.contentView

class CommonLauncher {
    fun FragmentActivity.launch(request: AgileRequest) {
        var composeView = findViewById<ComposeView>(composeViewId)
        if (composeView == null) {
            composeView = ComposeView(this).apply { id = composeViewId }
            val containerView = findContainerView(request)
            containerView.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        invokeCompose(composeView, request)
    }

    private fun FragmentActivity.invokeCompose(composeView: ComposeView, request: AgileRequest) {
        composeView.tag = request.uniqueTag

        val cls = Class.forName(request.className)
        val composable = cls.newInstance() as AgileComposable
        composeView.setContent {
            if (composable.paramsViewModelComposable != null) {
                composable.paramsViewModelComposable?.invoke(request.bundle, getViewModel(composable))
            } else if (composable.viewModelComposable != null) {
                composable.viewModelComposable?.invoke(getViewModel(composable))
            } else if (composable.paramsComposable != null) {
                composable.paramsComposable?.invoke(request.bundle)
            } else {
                composable.composable?.invoke()
            }
        }
    }

    @Suppress("unchecked_cast")
    private fun FragmentActivity.getViewModel(composable: AgileComposable): ViewModel {
        return ViewModelProvider(
            viewModelStore,
            defaultViewModelProviderFactory
        )[Class.forName(composable.viewModelClass) as Class<ViewModel>]
    }

    private fun FragmentActivity.findContainerView(request: AgileRequest): ViewGroup {
        var result: ViewGroup? = null
        if (request.containerViewId != 0) {
            result = findViewById(request.containerViewId)
        }
        if (result == null) {
            result = contentView()
        }
        return result
    }
}