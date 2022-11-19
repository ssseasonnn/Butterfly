package zlc.season.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import zlc.season.base.Schemes
import zlc.season.butterfly.annotation.Agile
import zlc.season.home.databinding.FragmentHomeBinding
import zlc.season.home.databinding.ItemGoodsBinding
import zlc.season.home.databinding.ItemHeaderBinding
import zlc.season.home.databinding.ItemTitleBinding
import zlc.season.yasha.grid

@Agile(Schemes.SCHEME_HOME)
class HomeFragment : Fragment() {
    var binding: FragmentHomeBinding? = null

    val dataSource by lazy { HomeDataSource(lifecycleScope) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentHomeBinding.inflate(inflater, container, false).also {
            this.binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            recyclerView.grid(dataSource) {
                spanCount(2)

                renderBindingItem<HeaderItem, ItemHeaderBinding> {
                    gridSpanSize(2)
                    viewBinding(ItemHeaderBinding::inflate)
                    onBind {
                        itemBinding.ivImg.load(data.img)
                    }
                }

                renderBindingItem<TitleItem, ItemTitleBinding> {
                    gridSpanSize(2)
                    viewBinding(ItemTitleBinding::inflate)
                }

                renderBindingItem<GoodsItem, ItemGoodsBinding> {
                    viewBinding(ItemGoodsBinding::inflate)
                    onBind {
                        itemBinding.ivImg.load(data.img)
                        itemBinding.tvName.text = data.name
                        itemBinding.tvMoney.text = data.money
                    }
                }
            }
        }
    }
}
