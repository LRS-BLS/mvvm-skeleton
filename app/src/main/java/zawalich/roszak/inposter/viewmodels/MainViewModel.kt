package zawalich.roszak.inposter.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import io.reactivex.annotations.NonNull
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenContextEnum
import zawalich.roszak.inposter.navigation.ScreenRegion
import javax.inject.Inject


class MainViewModel(application: Application) : AndroidViewModel(application) {
	val MainTitle = ObservableField<String>("Main ViewModel")

	val mainRegionViewModel = ObservableField<BaseViewModel>()
	val dialogRegionViewModel = ObservableField<BaseViewModel>()

	@Inject
	lateinit var navigationService: NavigationService

	init {
		InPosterApplication.appComponent.inject(this)

		navigationService.registerNavigationRegion(
				ScreenRegion.Main,
				object : NavigationService.NavigationRegion<BaseViewModel> {
					override fun doNavigate(ViewModel: BaseViewModel?) {
						mainRegionViewModel.set(ViewModel)
					}
				})
		navigationService.registerNavigationRegion(
				ScreenRegion.Dialog,
				object : NavigationService.NavigationRegion<BaseViewModel> {
					override fun doNavigate(ViewModel: BaseViewModel?) {
						dialogRegionViewModel.set(ViewModel)
					}
				})

		navigationService.navigate(ScreenRegion.Main, ScreenContextEnum.Camera())
	}

	fun handleBackButton(): Boolean {
		return navigationService.navigateBack()
	}

	override fun onCleared() {
		navigationService.clearAll()
	}

	class Factory(@param:NonNull private val application: Application) : ViewModelProvider.NewInstanceFactory() {

		override fun <T : ViewModel> create(modelClass: Class<T>): T {
			@Suppress("UNCHECKED_CAST")
			return MainViewModel(application) as T
		}
	}
}