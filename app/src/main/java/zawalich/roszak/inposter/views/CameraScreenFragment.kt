package zawalich.roszak.inposter.views

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.View
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.databinding.FragmentCameraScreenBinding
import zawalich.roszak.inposter.opencv.FaceDetectionCameraViewListener2
import zawalich.roszak.inposter.opencv.FaceDetectionInitializer
import zawalich.roszak.inposter.opencv.OCVCameraView
import zawalich.roszak.inposter.viewmodels.CameraScreenViewModel

class CameraScreenFragment :
		BaseFragment<FragmentCameraScreenBinding, CameraScreenViewModel>(), OCVCameraView.PhotoCallback {

	override val bindingClassToken: Class<FragmentCameraScreenBinding>
		get() = FragmentCameraScreenBinding::class.java

	override val viewModelClassToken: Class<CameraScreenViewModel>
		get() = CameraScreenViewModel::class.java

	override val layoutId: Int = R.layout.fragment_camera_screen

	private var loaderCallback: BaseLoaderCallback? = null
	private val fdInitializer = FaceDetectionInitializer()
	private val fdListener = FaceDetectionCameraViewListener2()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		loaderCallback = object : BaseLoaderCallback(context) {
			override fun onManagerConnected(status: Int) {
				if (status == LoaderCallbackInterface.SUCCESS) {
					fdListener.setPhotoQualityListener({ quality -> binding.viewModel?.setPhotoQuality(quality) })
					binding.cameraView.setPhotoCallback(this@CameraScreenFragment)
					fdInitializer.initialize(activity!!.baseContext)
					binding.cameraView.setCvCameraViewListener(fdListener)
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()
		binding.viewModel?.setViewActive(true)
		binding.viewModel?.getInitOCV()?.observe(this, Observer { shouldInit ->
			if (shouldInit == null || (!shouldInit)) {
				binding.cameraView.disableView()
			} else {
				initOpenCV()
			}
		})
		binding.viewModel?.getTakePicture()?.observe(this, Observer {
			binding.cameraView.takePicture()
		})
	}

	override fun onPause() {
		super.onPause()
		binding.viewModel?.setViewActive(false)
	}

	override fun onPhotoTaken(data: ByteArray?) {
		data?.let {
			binding.viewModel?.pictureTaken(data)
		}
	}

	private fun initOpenCV() {
		binding.cameraView.enableView()
		if (!OpenCVLoader.initDebug()) {
			Log.w("CameraScreenFragment", "Internal OpenCV library not found. Using OpenCV Manager for initialization")
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, context, loaderCallback)
		} else {
			Log.i("CameraScreenFragment", "OpenCV library found inside package. Using it!")
			loaderCallback!!.onManagerConnected(LoaderCallbackInterface.SUCCESS)
		}
	}
}
