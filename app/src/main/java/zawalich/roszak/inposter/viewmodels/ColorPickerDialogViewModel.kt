package zawalich.roszak.inposter.viewmodels

import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.navigation.NavigationService
import javax.inject.Inject


class ColorPickerDialogViewModel(resultHandler: (InposterColor) -> Unit) : BaseResultViewModel<InposterColor>(resultHandler) {
	@Inject
	override lateinit var navigationService: NavigationService

	init {
		InPosterApplication.appComponent.inject(this)
	}

	fun pickColor(color: InposterColor) {
		commitResult(color)
	}
}