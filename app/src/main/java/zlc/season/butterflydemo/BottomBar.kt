package zlc.season.butterflydemo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import zlc.season.butterflydemo.databinding.LayoutBottomBarBinding

class BottomBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val binding = LayoutBottomBarBinding.inflate(LayoutInflater.from(context), this, true)

    var onHomeClick: () -> Unit = {}
    var onCartClick: () -> Unit = {}
    var onUserClick: () -> Unit = {}

    init {
        binding.ivHome.isSelected = true
        binding.ivHome.setOnClickListener {
            onClick(it)
            onHomeClick()
        }
        binding.ivCart.setOnClickListener {
            onClick(it)
            onCartClick()
        }
        binding.ivUser.setOnClickListener {
            onClick(it)
            onUserClick()
        }
    }

    private fun onClick(view: View) {
        binding.ivHome.isSelected = false
        binding.ivCart.isSelected = false
        binding.ivUser.isSelected = false
        view.isSelected = true
    }
}