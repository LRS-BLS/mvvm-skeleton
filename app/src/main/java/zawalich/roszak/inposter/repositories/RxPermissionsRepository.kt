package zawalich.roszak.inposter.repositories

import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import zawalich.roszak.inposter.extensions.app
import zawalich.roszak.inposter.extensions.registerActivityLifecycleCallbacks
import zawalich.roszak.inposter.model.Permission
import zawalich.roszak.inposter.repositories.interfaces.PermissionsRepository
import zawalich.roszak.inposter.views.MainActivity
import java.lang.ref.WeakReference

class RxPermissionsRepository : PermissionsRepository {
	private val resultChecker = Function<Array<Any>, Boolean> { results -> results.any { it as Boolean } }
	private var activity: WeakReference<Activity?> = WeakReference(null)
	private var instance: RxPermissions? = null
	private val hasInstance: BehaviorSubject<Boolean> = BehaviorSubject.create()
	private val permissions = HashMap<Permission, PublishSubject<Boolean>>(6)
	private val ignored: MutableList<Permission> = mutableListOf()

	init {
		app().registerActivityLifecycleCallbacks(
				onActivityStarted = { activity ->
					activity?.let {
						if (activity !is MainActivity) {
							return@registerActivityLifecycleCallbacks
						}
						Log.d("RxPermissionsRepository", "activity started")
						this.activity = WeakReference(activity)
						instance = RxPermissions(activity)
						hasInstance.onNext(true)
					}
				},
				onActivityStopped = { activity ->
					activity?.let {
						if (activity !is MainActivity) {
							return@registerActivityLifecycleCallbacks
						}
						if (activity == this.activity.get()) {
							Log.d("RxPermissionsRepository", "activity stopped")
							this.activity.clear()
							hasInstance.onNext(false)
							instance = null
						}
					}
				}
		)
	}

	override fun observe(vararg permissions: Permission): Observable<Boolean> {
		val observables = ArrayList<Observable<Boolean>>(permissions.size)
		for (permission in permissions) {
			var subj = this.permissions[permission]
			if (subj == null) {
				subj = PublishSubject.create()
				this.permissions.put(permission, subj)
			}
			observables.add(subj!!.startWith(isGranted(permission)))
		}
		return Observable.combineLatest(observables, resultChecker)
	}

	private fun isGranted(permission: Permission): Boolean {
		return Build.VERSION.SDK_INT < 23 /*M*/ || isGranted60(permission)
	}

	@TargetApi(Build.VERSION_CODES.M)
	private fun isGranted60(permission: Permission): Boolean {
		return app().checkSelfPermission(permission.value) == PackageManager.PERMISSION_GRANTED
	}

	override fun requestEach(vararg permissions: Permission): Observable<Pair<Permission, Boolean?>> {
		val subject: ReplaySubject<Pair<Permission, Boolean?>> = ReplaySubject.create()

		hasInstance.observeOn(Schedulers.single())
				.subscribe { hasInstance ->
					if (hasInstance) {
						val requestedPermissions: MutableList<Permission> = mutableListOf()
						permissions.forEach { permission ->
							if (ignored.contains(permission)) { // permission rationale request is ignored
								subject.onNext(Pair(permission, false))
							} else {
								requestedPermissions.add(permission)
							}
						}

						if (requestedPermissions.isEmpty()) {
							return@subscribe
						}

						instance?.requestEach(*requestedPermissions.map { permission -> permission.value }.toTypedArray())
								?.subscribe { result ->
									val permission = Permission.get(result.name)
									var granted = result.granted as Boolean?

									if (result.shouldShowRequestPermissionRationale) granted = null

									// propagate the permission state
									if (granted != null && granted) {
										this.permissions[permission]?.onNext(true)
									} else {
										this.permissions[permission]?.onNext(false)
									}

									subject.onNext(Pair(permission, granted))
								}
					}
				}
		return subject
	}

	override fun ignoreRequest(vararg permissions: Permission) {
		ignored.addAll(permissions)
	}
}