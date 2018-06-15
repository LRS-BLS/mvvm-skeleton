package zawalich.roszak.inposter.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import io.reactivex.annotations.NonNull
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue
import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.NavigationService
import javax.inject.Inject
import kotlin.properties.ObservableProperty


class PreviewScreenViewModel(val params : PreviewContextModel) : BaseViewModel() {

	@Inject
	lateinit var navigationService: NavigationService

	val path = ObservableField<String>()
	val color = ObservableField<InposterColor>(InposterColor.Purple)
	val value = ObservableField<InposterValue>(InposterValue.Agility)

	init{
		InPosterApplication.appComponent.inject(this)

		path.set(params.imagePath)
	}

	fun changeColor(newColor : InposterColor){
		color.set(newColor)
		value.set(randomValue())
	}

	private fun randomValue(): InposterValue? {
		val values = InposterValue.values()
		return values[Math.floor(Math.random()*values.count()).toInt()]
	}
}