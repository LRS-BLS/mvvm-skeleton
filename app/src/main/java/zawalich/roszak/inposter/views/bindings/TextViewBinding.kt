package zawalich.roszak.inposter.views.bindings

import android.databinding.BindingAdapter
import android.support.v7.widget.AppCompatTextView
import android.widget.TextView


@BindingAdapter("user_name", "user_age")
fun concatUserNameAndAge(view: TextView,
					 name: String?,
					 age: Int?) {
	if (name == null || age == null) {
		return
	}
	view.text = "${name}(${age})"
}