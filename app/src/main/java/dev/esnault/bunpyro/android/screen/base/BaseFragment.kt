package dev.esnault.bunpyro.android.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass


abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    abstract val vm: BaseViewModel?
    private var _binding: VB? = null

    /**
     * Binding of the Fragment.
     * This property is only valid between onCreateView and onDestroyView.
     */
    protected val binding: VB
        get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm?.let { NavigatorListener(this, it.navigationCommands) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Since we can't call .inflate() on the ViewBinding directly, let's use reflection.
        // This is not ideal, but saving the hassle to inflate the ViewBinding manually for each
        // fragment makes it worth it.
        // Let's hope that someday we have a way to avoid this reflection usage.

        val inflateMethod = bindingClass.java.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.javaPrimitiveType
        )
        @Suppress("UNCHECKED_CAST")
        _binding = inflateMethod.invoke(null, inflater, container, false) as VB
        return binding.root
    }

    abstract val bindingClass: KClass<VB>

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
