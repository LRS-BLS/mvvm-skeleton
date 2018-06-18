package zawalich.roszak.inposter.dagger

import dagger.Component
import zawalich.roszak.inposter.viewmodels.*
import zawalich.roszak.inposter.widgets.RegionFrameLayout
import javax.inject.Singleton

@Singleton
@Component(modules = [(InPosterApplicationModule::class)])
interface InPosterApplicationComponent {
	fun inject(view: RegionFrameLayout)

	fun inject(viewModel: MainViewModel)
	fun inject(viewModel: CameraScreenViewModel)
	fun inject(viewModel: PreviewScreenViewModel)
	fun inject(viewModel: SampleViewModel)
	fun inject(viewModel: RationaleDialogViewModel)
	fun inject(viewModel: ColorPickerDialogViewModel)
	fun inject(viewModel: ValuePickerDialogViewModel)
}