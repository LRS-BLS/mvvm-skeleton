package zawalich.roszak.inposter.views

import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.DialogValuePickerBinding
import zawalich.roszak.inposter.viewmodels.ValuePickerDialogViewModel


class ValuePickerDialogFragment : BaseFragment<DialogValuePickerBinding, ValuePickerDialogViewModel>() {
	override val bindingClassToken: Class<DialogValuePickerBinding>
		get() = DialogValuePickerBinding::class.java

	override val viewModelClassToken: Class<ValuePickerDialogViewModel>
		get() = ValuePickerDialogViewModel::class.java

	override val layoutId: Int = R.layout.dialog_value_picker
}