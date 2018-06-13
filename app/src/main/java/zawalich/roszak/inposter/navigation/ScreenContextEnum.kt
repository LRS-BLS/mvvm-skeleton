package zawalich.roszak.inposter.navigation

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProvider
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.ContextModels.RationaleDialogContextModel
import zawalich.roszak.inposter.viewmodels.*

import zawalich.roszak.inposter.views.RationaleDialogFragment

import zawalich.roszak.inposter.views.BaseView
import zawalich.roszak.inposter.views.CameraScreenFragment
import zawalich.roszak.inposter.views.PreviewScreenFragment
import zawalich.roszak.inposter.views.SampleFragment

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

sealed class ScreenContextEnum(val type: BindingType, val viewModelFactory: ()->BaseViewModel){

	data class Camera(val contextModel: ContextModel = ContextModel.Empty) : ScreenContextEnum(BindingType.CAMERA,
			{CameraScreenViewModel()})

	data class Preview(val contextModel: PreviewContextModel) : ScreenContextEnum(BindingType.PREVIEW,
			{PreviewScreenViewModel(contextModel)})

	data class RationaleDialog(val contextModel: RationaleDialogContextModel) : ScreenContextEnum(BindingType.PREVIEW,
			{RationaleDialogViewModel(contextModel)})

	data class Sample(val contextModel: ContextModel = ContextModel.Empty) : ScreenContextEnum(BindingType.SAMPLE,
			{SampleViewModel()})

}