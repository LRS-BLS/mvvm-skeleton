package zawalich.roszak.inposter.navigation

import zawalich.roszak.inposter.navigation.NavigationService.NavigationRegion
import zawalich.roszak.inposter.viewmodels.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList


class NavigationOnRegionsService : NavigationService {
	private data class NavigationStackItem(val region: ScreenRegion,
										   val contextDefinition: ScreenContextEnum,
										   val vm: BaseViewModel)

	private var regions = HashMap<ScreenRegion, NavigationRegion<BaseViewModel>>()
	private val navigationStack = ArrayList<NavigationStackItem>()

	override fun <T : BaseViewModel> registerNavigationRegion(type: ScreenRegion, region: NavigationRegion<T>) {
		regions[type] = region as NavigationRegion<BaseViewModel>
	}

	override fun navigate(type: ScreenRegion, screenContextEnum: ScreenContextEnum) {
		val item = buildNavigationStackItem(type, screenContextEnum)
		//Notify currently diplayed item that is is being hidden
		navigationStack.lastOrNull { it.region == type }?.let { (it.vm as? NavigationAware?)?.onNavigatedFrom() }
		//Add new item to stack
		navigationStack.add(item)
		//Request new viewmodel to be displayed
		regions.get(type)?.doNavigate(item.vm)
		//Notify new item that it is displayed
		item.vm.let { (it as? NavigationAware?)?.onNavigatedTo() }
	}

	override fun navigateBack(type: ScreenRegion): Boolean {
		if (navigationStack.count() == 0) {
			return false
		}

		var regionToGoBack = type
		if (type == ScreenRegion.Any) {
			regionToGoBack = navigationStack.last().region
		}
		val item = navigationStack.last { it.region == regionToGoBack }
		//Notify currently displayed viewmodel that is is no longer displayed
		item.vm.let { (it as? NavigationAware?)?.onNavigatedFrom() }
		navigationStack.remove(item)

		val vmToGoBackTo = navigationStack.lastOrNull { it.region == regionToGoBack }
		regions.get(regionToGoBack)?.doNavigate(vmToGoBackTo?.vm)
		//Notify new item that it is displayed
		vmToGoBackTo?.vm?.let { (it as? NavigationAware?)?.onNavigatedTo() }

		return navigationStack.count() != 0
	}

	override fun clearAll() {
		navigationStack.forEach {
			it.vm.let { (it as? NavigationAware?)?.onNavigatedFrom() }
			it.vm.clear()
		}
	}

	private fun buildNavigationStackItem(type: ScreenRegion, screenContextEnum: ScreenContextEnum): NavigationOnRegionsService.NavigationStackItem {

		val vm = screenContextEnum.viewModelFactory()

		return NavigationStackItem(type, screenContextEnum, vm)
	}
}