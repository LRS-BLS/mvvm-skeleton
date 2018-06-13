package zawalich.roszak.inposter

import android.support.multidex.MultiDexApplication
import zawalich.roszak.inposter.dagger.DaggerInPosterApplicationComponent
import zawalich.roszak.inposter.dagger.InPosterApplicationComponent
import zawalich.roszak.inposter.dagger.InPosterApplicationModule

class InPosterApplication : MultiDexApplication() {
	companion object {
		@JvmStatic lateinit var instance: InPosterApplication private set
		@JvmStatic lateinit var appComponent: InPosterApplicationComponent private set
		@JvmStatic lateinit var appInPosterModule: InPosterApplicationModule private set

		init {
			System.loadLibrary("opencv_java3")
			System.loadLibrary("detector")
		}
	}

	override fun onCreate() {
		super.onCreate()

		super.onCreate()
		instance = this
		appInPosterModule = InPosterApplicationModule()
		appComponent = DaggerInPosterApplicationComponent.builder().build()
	}
}