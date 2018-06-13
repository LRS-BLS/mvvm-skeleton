package zawalich.roszak.inposter.navigation


interface NavigationAware {
	/**
	 * This method is called when viewmodel is about to be displayed.
	 */
	fun onNavigatedTo()

	/**
	 * This method is called when viewmodel is no longer displayed.
	 * Navigation has been made in either direction to a different viewmodel.
	 */
	fun onNavigatedFrom()
}