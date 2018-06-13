package zawalich.roszak.inposter.viewmodels

import android.databinding.ObservableField
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenContextEnum
import zawalich.roszak.inposter.navigation.ScreenRegion
import javax.inject.Inject


class SampleViewModel : BaseViewModel() {
	@Inject
	lateinit var navigationService: NavigationService

	val name = ObservableField("John Doe")
	val age = ObservableField<Int>(0)

	init {
		InPosterApplication.appComponent.inject(this)
	}

	fun growUp() {
		age.set((age.get() ?: 0) + 1)
	}

	fun soSomethingElse(){
		navigationService.navigate(
				ScreenRegion.Main,
				ScreenContextEnum.Preview(PreviewContextModel("https://static.goldenline.pl/user_photo/050/user_345394_507387_huge.jpg"))
		)
	}
}