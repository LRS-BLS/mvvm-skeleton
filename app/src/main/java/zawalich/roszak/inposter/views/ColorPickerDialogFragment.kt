package zawalich.roszak.inposter.views

import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.DialogColorPickerBinding
import zawalich.roszak.inposter.viewmodels.ColorPickerDialogViewModel


class ColorPickerDialogFragment : BaseFragment<DialogColorPickerBinding, ColorPickerDialogViewModel>() {
	override val bindingClassToken: Class<DialogColorPickerBinding>
		get() = DialogColorPickerBinding::class.java

	override val viewModelClassToken: Class<ColorPickerDialogViewModel>
		get() = ColorPickerDialogViewModel::class.java

	override val layoutId: Int = R.layout.dialog_color_picker
}