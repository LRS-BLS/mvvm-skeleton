package zawalich.roszak.inposter.navigation

import android.arch.lifecycle.ViewModel
import zawalich.roszak.inposter.views.BaseView


class EnumBasedViewClassResolverStrategy : ViewClassResolverStrategy {
	override fun findBaseViewForViewModel(vm: ViewModel): Class<out BaseView> {
		val contextBinding = ScreenContextEnum.BindingType.values().first {
			it.viewModelClass == vm::class.java
		}

		return contextBinding.viewClass
	}
}