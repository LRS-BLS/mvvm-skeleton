package zawalich.roszak.inposter.navigation


/**
 * Base class for navigation parameters.
 * If they aren't needed and you need to create one use ContextModel.Empty object.
 */
abstract class ContextModel{
	companion object Empty : ContextModel()
}