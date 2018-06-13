package zawalich.roszak.inposter.opencv;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetectionCameraViewListener2 extends BaseCvCameraViewListener2 {

	private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

	private Mat rgba = null;
	private Mat gray = null;
	private CascadeClassifier javaDetector;
	private float relativeFaceSize = 0.2f;
	private int absoluteFaceSize = 0;

	@Override
	public void onCameraViewStarted(int width, int height) {
		gray = new Mat();
		rgba = new Mat();
	}

	@Override
	public void onCameraViewStopped() {
		if (gray != null) {
			gray.release();
		}
		if (rgba != null) {
			rgba.release();
		}
	}

	@Override
	public Mat onCameraFrame(CameraBridgeViewBase2.CvCameraViewFrame inputFrame) {
		javaDetector = FaceDetectionInitializer.Companion.getDetector();

		rgba = inputFrame.rgba();
		gray = inputFrame.gray();

		if (absoluteFaceSize == 0) {
			int height = gray.rows();
			if (Math.round(height * relativeFaceSize) > 0) {
				absoluteFaceSize = Math.round(height * relativeFaceSize);
			}
		}

		MatOfRect faces = new MatOfRect();

		if (javaDetector != null) {
				javaDetector.detectMultiScale(gray, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						new Size(absoluteFaceSize, absoluteFaceSize), new Size());
		} else {
			Log.e("camera", "Detection method is not selected!");
		}

		Rect[] facesArray = faces.toArray();

		if (facesArray.length > 0) {
			setGoodImageQuality();
		} else {
			setBadImageQuality();
		}

		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(rgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);

		return rgba;
	}
}
