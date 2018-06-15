package zawalich.roszak.inposter.navigation

import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.ContextModels.RationaleDialogContextModel
import zawalich.roszak.inposter.viewmodels.*
import zawalich.roszak.inposter.views.*


sealed class ScreenContextEnum(val type: BindingType, val viewModelFactory: () -> BaseViewModel) {
	enum class BindingType(val viewModelClass: Class<out BaseViewModel>,
						   val viewClass: Class<out BaseView>) {
		CAMERA(
				CameraScreenViewModel::class.java,
				CameraScreenFragment::class.java
		),
		PREVIEW(
				PreviewScreenViewModel::class.java,
				PreviewScreenFragment::class.java
		),
		RATIONALE_DIALOG(
				RationaleDialogViewModel::class.java,
				RationaleDialogFragment::class.java),
		SAMPLE(
				SampleViewModel::class.java,
				SampleFragment::class.java
		)
	}

	data class Camera(val contextModel: ContextModel = ContextModel.Empty) : ScreenContextEnum(BindingType.CAMERA,
			{ CameraScreenViewModel() })

	data class Preview(val contextModel: PreviewContextModel) : ScreenContextEnum(BindingType.PREVIEW,
			{ PreviewScreenViewModel(contextModel) })

	data class RationaleDialog(val contextModel: RationaleDialogContextModel) : ScreenContextEnum(BindingType.PREVIEW,
			{ RationaleDialogViewModel(contextModel) })

	data class Sample(val contextModel: ContextModel = ContextModel.Empty) : ScreenContextEnum(BindingType.SAMPLE,
			{ SampleViewModel() })

}