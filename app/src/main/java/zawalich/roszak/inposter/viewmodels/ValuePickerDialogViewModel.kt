package zawalich.roszak.inposter.viewmodels

import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.model.InposterValue
import zawalich.roszak.inposter.navigation.NavigationService
import javax.inject.Inject


class ValuePickerDialogViewModel(resultHandler: (InposterValue) -> Unit) : BaseResultViewModel<InposterValue>(resultHandler) {
	@Inject
	override lateinit var navigationService: NavigationService

	init {
		InPosterApplication.appComponent.inject(this)
	}

	fun pickValue(color: InposterValue) {
		commitResult(color)
	}
}