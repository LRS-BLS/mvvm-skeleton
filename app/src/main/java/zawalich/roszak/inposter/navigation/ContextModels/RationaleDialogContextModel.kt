package zawalich.roszak.inposter.navigation.ContextModels

data class RationaleDialogContextModel(
		/**
		 * Code called when user clicked positive button.
		 */
		val positiveCallback: () -> Unit,
		/**
		 * Code called when user clicked negative.
		 */
		val negativeCallback: () -> Unit
)