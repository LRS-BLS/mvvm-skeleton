package zawalich.roszak.inposter.views.bindings

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.databinding.BindingAdapter
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import zawalich.roszak.inposter.navigation.BindingType
import zawalich.roszak.inposter.views.BaseView


@BindingAdapter("viewmodel", requireAll = false)
fun processRegionContext(view: FrameLayout, vm: ViewModel?) {
	if (vm == null){
		clearRegion(view)
		return
	}

	val fm = (view.context as AppCompatActivity).supportFragmentManager
	if (fm.isStateSaved) {
		return
	}

	val contextBinding = BindingType.values().first {
		it.viewModelClass == vm::class.java
	}

	changeRegionContext(view, contextBinding, vm)
}

fun clearRegion(view: FrameLayout){
	hideSoftKeyboard(view.context as Activity)
	val fm = (view.context as AppCompatActivity).supportFragmentManager
	val ft = fm.beginTransaction()
	for (fragment in fm.fragments) {
		ft.remove(fragment)
	}
	ft.commit()
}

fun changeRegionContext(view: FrameLayout, screen: BindingType, vm: ViewModel) {

	hideSoftKeyboard(view.context as Activity)
	val oldDefinition = view.tag as BindingType?

	if (oldDefinition == screen) {
		//L.d(L.UI, "same screen as previous, change screen aborted")
		return
	}

	val fm = (view.context as AppCompatActivity).supportFragmentManager
	val ft = fm.beginTransaction()
	val constructor = screen.viewClass.getConstructor()
	val fragment = constructor.newInstance()
	(fragment as BaseView).setViewModel(vm)

	ft.replace(view.id, fragment as Fragment)
	ft.commit()

	view.tag = screen
}


private fun hideSoftKeyboard(activity: Activity?) {
	if (activity != null) {
		val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		if (activity.currentFocus != null) {
			inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
			inputManager.hideSoftInputFromInputMethod(activity.currentFocus!!.windowToken, 0)
		}
	}
}