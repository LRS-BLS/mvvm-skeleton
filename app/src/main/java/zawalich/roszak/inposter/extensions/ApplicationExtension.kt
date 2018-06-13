package zawalich.roszak.inposter.extensions

import android.app.Activity
import android.app.Application
import android.os.Bundle

private val onActivityCreatedStub: (Activity?, Bundle?) -> Unit = { _, _ -> }
private val onActivityStartedStub: (Activity?) -> Unit = {}
private val onActivityResumedStub: (Activity?) -> Unit = {}
private val onActivityPausedStub: (Activity?) -> Unit = {}
private val onActivitySaveInstanceStateStub: (Activity?, Bundle?) -> Unit = { _, _ -> }
private val onActivityStoppedStub: (Activity?) -> Unit = {}
private val onActivityDestroyedStub: (Activity?) -> Unit = {}

fun Application.registerActivityLifecycleCallbacks(
		onActivityCreated: (Activity?, Bundle?) -> Unit = onActivityCreatedStub,
		onActivityStarted: (Activity?) -> Unit = onActivityStartedStub,
		onActivityResumed: (Activity?) -> Unit = onActivityResumedStub,
		onActivityPaused: (Activity?) -> Unit = onActivityPausedStub,
		onActivitySaveInstanceState: (Activity?, Bundle?) -> Unit = onActivitySaveInstanceStateStub,
		onActivityStopped: (Activity?) -> Unit = onActivityStoppedStub,
		onActivityDestroyed: (Activity?) -> Unit = onActivityDestroyedStub

): Application.ActivityLifecycleCallbacks {
	val callback = object : Application.ActivityLifecycleCallbacks {
		override fun onActivityPaused(activity: Activity?) {
			onActivityPaused(activity)
		}

		override fun onActivityResumed(activity: Activity?) {
			onActivityResumed(activity)
		}

		override fun onActivityStarted(activity: Activity?) {
			onActivityStarted(activity)
		}

		override fun onActivityDestroyed(activity: Activity?) {
			onActivityDestroyed(activity)
		}

		override fun onActivitySaveInstanceState(activity: Activity?, instanceState: Bundle?) {
			onActivitySaveInstanceState(activity, instanceState)
		}

		override fun onActivityStopped(activity: Activity?) {
			onActivityStopped(activity)
		}

		override fun onActivityCreated(activity: Activity?, instanceState: Bundle?) {
			onActivityCreated(activity, instanceState)
		}
	}
	registerActivityLifecycleCallbacks(callback)
	return callback
}