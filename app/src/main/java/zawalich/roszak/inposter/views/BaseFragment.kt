package zawalich.roszak.inposter.views

import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 *	Default implementation of a fragment that creates a layout and binds it to a ViewModel.
 */
abstract class BaseFragment<B: ViewDataBinding, VM: ViewModel> : Fragment(), BaseView {
	abstract val bindingClassToken: Class<B>
	abstract val viewModelClassToken: Class<VM>
	abstract val layoutId : Int

	open lateinit var binding : B
	open lateinit var vm: ViewModel

	override fun setViewModel(viewModel: ViewModel) {
		this.vm = viewModel
		bindIfReady()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, layoutId, container, false) as B

		bindIfReady()

		return binding.root
	}

	private fun bindIfReady() {
		if (::vm.isInitialized && ::binding.isInitialized) {
			val method = bindingClassToken.getMethod("setViewModel", viewModelClassToken)
			method.invoke(binding, vm)
		}
	}
}