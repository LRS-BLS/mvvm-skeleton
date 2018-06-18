package zawalich.roszak.inposter.viewmodels

import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenRegion


abstract class BaseResultViewModel<R>(val resultHandler: (R) -> Unit) : BaseViewModel() {
	abstract var navigationService: NavigationService

	fun commitResult(r: R) {
		navigationService.navigateBack(ScreenRegion.Dialog)
		resultHandler(r)
	}
}