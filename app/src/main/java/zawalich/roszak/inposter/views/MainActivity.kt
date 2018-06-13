package zawalich.roszak.inposter.views

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.ActivityMainBinding
import zawalich.roszak.inposter.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
	lateinit var viewModel : MainViewModel

	override fun onBackPressed() {
		if(!viewModel.handleBackButton()){
			super.onBackPressed()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val factory = MainViewModel.Factory(
			InPosterApplication.instance
		)

		viewModel = ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
		val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
		binding.setViewModel(viewModel)

		// Example of a call to a native method
//		sample_text.text = stringFromJNI()
	}

	/**
	 * A native method that is implemented by the 'native-lib' native library,
	 * which is packaged with this application.
	 */
	external fun stringFromJNI(): String
}
