package zawalich.roszak.inposter.utils

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object AsyncUtils {
	/**
	 * Performs first lambda on async thread and second one on resultThread.
	 * Result of first Lambda is passed as a parameter of second one.
	 */
	fun <T> executeAsync(asyncThreadCode: () -> T, mainThreadCode: (T) -> Unit, asyncScheduler: Scheduler = Schedulers.io(), resultScheduler: Scheduler = AndroidSchedulers.mainThread()) {
		Single.just(0).observeOn(asyncScheduler)
				.map { _ ->
					asyncThreadCode()
				}.observeOn(resultScheduler)
				.subscribe { result ->
					mainThreadCode(result)
				}
	}
}