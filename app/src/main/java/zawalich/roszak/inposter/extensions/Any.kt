package zawalich.roszak.inposter.extensions

import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.dagger.InPosterApplicationComponent

fun Any.dagger(): InPosterApplicationComponent {
	return InPosterApplication.appComponent
}

fun Any.app(): InPosterApplication {
	return InPosterApplication.instance
}