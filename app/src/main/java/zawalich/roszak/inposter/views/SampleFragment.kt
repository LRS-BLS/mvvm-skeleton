package zawalich.roszak.inposter.views

import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.FragmentSampleScreenBinding
import zawalich.roszak.inposter.viewmodels.SampleViewModel

class SampleFragment :
		BaseFragment<FragmentSampleScreenBinding, SampleViewModel>() {

	override val bindingClassToken: Class<FragmentSampleScreenBinding>
		get() = FragmentSampleScreenBinding::class.java

	override val viewModelClassToken: Class<SampleViewModel>
		get() = SampleViewModel::class.java

	override val layoutId: Int = R.layout.fragment_sample_screen
}