package zawalich.roszak.inposter.widgets

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import zawalich.roszak.inposter.navigation.ScreenContextEnum
import zawalich.roszak.inposter.views.BaseView

class RegionFrameLayout : FrameLayout {
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context) : super(context)

	fun setviewmodel(vm: ViewModel?) {

		if (vm == null) {
			clearRegion(this)
			return
		}

		val fm = (this.context as AppCompatActivity).supportFragmentManager
		if (fm.isStateSaved) {
			return
		}

		val contextBinding = ScreenContextEnum.BindingType.values().first {
			it.viewModelClass == vm::class.java
		}

		changeRegionContext(this, contextBinding.viewClass, vm)
	}

	fun clearRegion(view: FrameLayout) {
		hideSoftKeyboard(view.context as Activity)
		val fm = (view.context as AppCompatActivity).supportFragmentManager
		val ft = fm.beginTransaction()
		for (fragment in fm.fragments) {
			ft.remove(fragment)
		}
		ft.commit()
	}

	fun changeRegionContext(view: FrameLayout, viewClass: Class<*>, vm: ViewModel) {

		hideSoftKeyboard(view.context as Activity)
		val oldDefinition = view.tag as ScreenContextEnum.BindingType?

		if (oldDefinition == vm) {
			//L.d(L.UI, "same screen as previous, change screen aborted")
			return
		}

		val fm = (view.context as AppCompatActivity).supportFragmentManager
		val ft = fm.beginTransaction()
		val constructor = viewClass.getConstructor()
		val fragment = constructor.newInstance()
		(fragment as BaseView).setViewModel(vm)

		ft.replace(view.id, fragment as Fragment)
		ft.commit()

		view.tag = vm
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
}