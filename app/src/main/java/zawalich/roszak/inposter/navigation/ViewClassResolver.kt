package zawalich.roszak.inposter.navigation

import android.arch.lifecycle.ViewModel
import zawalich.roszak.inposter.views.BaseView

/**
 * An interface for a strategy that is used to find a View responsible for displaying a particular viewmodel.
 */
interface ViewClassResolverStrategy {
	fun findBaseViewForViewModel(vm: ViewModel): Class<out BaseView>
}