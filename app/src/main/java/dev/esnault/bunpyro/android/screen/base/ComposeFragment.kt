package dev.esnault.bunpyro.android.screen.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment


/**
 * A Fragment based on a Jetpack Compose UI.
 */
abstract class ComposeFragment : Fragment() {

    abstract val vm: BaseViewModel?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm?.let { NavigatorListener(this, it.navigationCommands) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ComposeView(requireContext()).apply {
        setContent {
            FragmentContent()
        }
    }

    /**
     * Set the Jetpack Compose UI content for this Fragment.
     * Initial composition will occur when the view becomes attached to a window.
     */
    @Composable
    abstract fun FragmentContent()
}
