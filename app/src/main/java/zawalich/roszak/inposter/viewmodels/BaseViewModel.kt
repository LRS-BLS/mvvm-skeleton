package zawalich.roszak.inposter.viewmodels

import android.arch.lifecycle.ViewModel


abstract class BaseViewModel : ViewModel() {
	fun clear(){
		onCleared()
	}
}