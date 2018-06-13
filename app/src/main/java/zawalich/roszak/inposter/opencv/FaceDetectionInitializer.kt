package zawalich.roszak.inposter.opencv

import android.content.Context
import android.util.Log
import org.opencv.objdetect.CascadeClassifier
import zawalich.roszak.inposter.R
import java.io.File
import java.io.IOException

class FaceDetectionInitializer {

	companion object {
		var detector: CascadeClassifier? = null
	}
	fun initialize(context: Context) {
		val cascadeFile = File(context.cacheDir, "lbpcascade_frontalface.xml")

		val inputStream = context.resources.openRawResource(R.raw.lbpcascade_frontalface)
		val outputStream = cascadeFile.outputStream()

		try {
			inputStream.copyTo(outputStream)

			detector = CascadeClassifier(cascadeFile.absolutePath)
			if (detector!!.empty()) {
				Log.e("detector initializer", "Failed to load cascade classifier")
				detector = null
			} else {
				Log.i("detector initializer", "Loaded cascade classifier from " + cascadeFile.absolutePath)
			}

		} catch (e: IOException) {
			e.printStackTrace()
			Log.e("detector initializer", "Failed to load cascade. Exception thrown: $e")
		} finally {
			inputStream.close()
			outputStream.close()
		}
	}

}