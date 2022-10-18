package zlc.season.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    open val name: String
        get() = "$this tag: ${this.tag}"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        println("$name onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("$name onCreate: $savedInstanceState")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("$name onCreateView: $savedInstanceState")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        println("$name onStart")
    }

    override fun onResume() {
        super.onResume()
        println("$name onResume")
    }

    override fun onPause() {
        super.onPause()
        println("$name onPause")
    }

    override fun onStop() {
        super.onStop()
        println("$name onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("$name onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("$name onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        println("$name onDetach")
    }
}