package zawalich.roszak.inposter.views

import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.FragmentPreviewScreenBinding
import zawalich.roszak.inposter.viewmodels.PreviewScreenViewModel

class PreviewScreenFragment :
		BaseFragment<FragmentPreviewScreenBinding, PreviewScreenViewModel>() {

	override val bindingClassToken: Class<FragmentPreviewScreenBinding>
		get() = FragmentPreviewScreenBinding::class.java

	override val viewModelClassToken: Class<PreviewScreenViewModel>
		get() = PreviewScreenViewModel::class.java

	override val layoutId: Int = R.layout.fragment_preview_screen
}
