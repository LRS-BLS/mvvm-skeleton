package zawalich.roszak.inposter.repositories.interfaces

import io.reactivex.Observable
import zawalich.roszak.inposter.model.Permission

interface PermissionsRepository {
	/**
	 * Observes state of given permissions
	 */
	fun observe(vararg permissions: Permission): Observable<Boolean>
	/**
	 * Requests runtime permissions from user.
	 * @param permissions list of requested permissions
	 * @return observable pair where first is the requested permission and second is the status where true means permission granted,
	 * false means permission not granted, null means that rationale explanation should be shown
	 */
	fun requestEach(vararg permissions: Permission): Observable<Pair<Permission, Boolean?>>
	/**
	 * Ignores requests for given permission. When next time permission is requested then returns false.
	 */
	fun ignoreRequest(vararg permissions: Permission)
}