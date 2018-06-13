package zawalich.roszak.inposter.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.annotations.NonNull
import zawalich.roszak.inposter.extensions.dagger
import zawalich.roszak.inposter.navigation.ContextModels.RationaleDialogContextModel
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenRegion
import javax.inject.Inject

class RationaleDialogViewModel(val params : RationaleDialogContextModel) :  BaseViewModel() {

	@Inject
	lateinit var navigationService: NavigationService

	init {
		dagger().inject(this)
	}

	fun onPositiveClick() {
		navigationService.navigateBack(ScreenRegion.Dialog)
		params.positiveCallback()
	}

	fun onNegativeClick() {
		navigationService.navigateBack(ScreenRegion.Dialog)
		params.negativeCallback()
	}
}