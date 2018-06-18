package zawalich.roszak.inposter.viewmodels

import android.databinding.ObservableField
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue
import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenContextEnum
import zawalich.roszak.inposter.navigation.ScreenRegion
import javax.inject.Inject


class PreviewScreenViewModel(val params: PreviewContextModel) : BaseViewModel() {

	@Inject
	lateinit var navigationService: NavigationService

	val path = ObservableField<String>()
	val color = ObservableField<InposterColor>(InposterColor.Purple)
	val value = ObservableField<InposterValue>(InposterValue.Agility)

	init {
		InPosterApplication.appComponent.inject(this)

		path.set(params.imagePath)
	}

	fun openColorPicker() {
		navigationService.navigate(ScreenRegion.Dialog, ScreenContextEnum.ColorPickerDialog { newColor ->
			color.set(newColor)
		})
	}

	fun openValuePicker() {
		navigationService.navigate(ScreenRegion.Dialog, ScreenContextEnum.ValuePickerDialog { newValue ->
			value.set(newValue)
		})
	}

	fun pickRandom() {
		color.set(randomColor())
		value.set(randomValue())
	}

	private fun randomColor(): InposterColor? {
		val values = InposterColor.values()
		return values[Math.floor(Math.random() * values.count()).toInt()]
	}

	private fun randomValue(): InposterValue? {
		val values = InposterValue.values()
		return values[Math.floor(Math.random() * values.count()).toInt()]
	}
}