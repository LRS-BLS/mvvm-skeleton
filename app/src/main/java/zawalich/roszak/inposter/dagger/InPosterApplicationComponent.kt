package zawalich.roszak.inposter.dagger

import dagger.Component
import zawalich.roszak.inposter.viewmodels.*
import javax.inject.Singleton

@Singleton
@Component(modules = [(InPosterApplicationModule::class)])
interface InPosterApplicationComponent {
	fun inject(viewModel: MainViewModel)
	fun inject(viewModel: CameraScreenViewModel)
	fun inject(viewModel: PreviewScreenViewModel)
	fun inject(viewModel: SampleViewModel)
	fun inject(viewModel: RationaleDialogViewModel)
}