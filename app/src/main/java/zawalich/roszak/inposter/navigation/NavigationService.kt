package zawalich.roszak.inposter.navigation

import zawalich.roszak.inposter.viewmodels.BaseViewModel


interface NavigationService {
	interface NavigationRegion<in T : BaseViewModel>{
		fun doNavigate(ViewModel : T?)
	}

	fun navigate(type: ScreenRegion, screenContextEnum: ScreenContextEnum)

	fun navigateBack(type: ScreenRegion = ScreenRegion.Any) : Boolean

	fun <T : BaseViewModel> registerNavigationRegion(type : ScreenRegion, region : NavigationRegion<T>)

	fun clearAll()
}
