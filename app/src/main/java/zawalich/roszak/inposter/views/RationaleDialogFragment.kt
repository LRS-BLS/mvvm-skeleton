package zawalich.roszak.inposter.views

import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.DialogRationaleBinding
import zawalich.roszak.inposter.viewmodels.RationaleDialogViewModel

class RationaleDialogFragment : BaseFragment<DialogRationaleBinding, RationaleDialogViewModel>() {
	override val bindingClassToken: Class<DialogRationaleBinding>
		get() = DialogRationaleBinding::class.java

	override val viewModelClassToken: Class<RationaleDialogViewModel>
		get() = RationaleDialogViewModel::class.java

	override val layoutId: Int = R.layout.dialog_rationale
}