package zawalich.roszak.inposter.opencv;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class OCVCameraView extends CameraBridgeViewBase2 implements Camera.PreviewCallback, Camera.PictureCallback {

	private static final int MAGIC_TEXTURE_ID = 10;
	private static final int NO_CAMERA_ID = -1;
	private static final String TAG = "OCVCameraView";

	private byte mBuffer[];
	private Mat[] mFrameChain;
	private int mChainIdx = 0;
	private Thread mThread;
	private boolean mStopThread;
	private PhotoCallback photoCallback;
	@SuppressWarnings("FieldCanBeLocal")
	private SurfaceTexture surfaceTexture;

	protected Camera camera;
	protected OCVCameraView.JavaCameraFrame[] mCameraFrame;

	public interface PhotoCallback {
		void onPhotoTaken(byte[] data);
	}

	public static class JavaCameraSizeAccessor implements ListItemAccessor {

		@Override
		public int getWidth(Object obj) {
			Camera.Size size = (Camera.Size) obj;
			return size.width;
		}

		@Override
		public int getHeight(Object obj) {
			Camera.Size size = (Camera.Size) obj;
			return size.height;
		}
	}

	public OCVCameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected boolean initializeCamera(int width, int height) {
		Log.d("camera", "Initialize java camera");
		boolean result = true;

		synchronized (this) {
			camera = null;

			int localCameraIndex = NO_CAMERA_ID;
			Log.i("camera", "Trying to open front camera");
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
				Camera.getCameraInfo(camIdx, cameraInfo);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					localCameraIndex = camIdx;
					break;
				}
			}

			if (localCameraIndex == NO_CAMERA_ID) {
				Log.e("camera", "Front camera not found!");
			} else {
				Log.d("camera", "Trying to open camera with new open(" + localCameraIndex + ")");
				try {
					camera = Camera.open(localCameraIndex);
				} catch (RuntimeException e) {
					Log.e("camera", "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
				}
			}

			if (camera == null)
				return false;

			/* Now set camera parameters */
			try {
				Camera.Parameters params = camera.getParameters();
				Log.d("camera", "getSupportedPreviewSizes()");
				List<Camera.Size> sizes = params.getSupportedPreviewSizes();
				List<Camera.Size> pSizes = params.getSupportedPictureSizes();

				if (sizes != null) {
					/* Select the size that fits surface considering maximum size allowed */
					Size pictureSize = calculatePictureSize(pSizes, new JavaCameraSizeAccessor());
					Size frameSize = calculateCameraFrameSize(sizes, new JavaCameraSizeAccessor(), pictureSize, Math.round(width / 1.5f), Math.round(height / 1.5f));

					params.setPreviewFormat(ImageFormat.NV21);
					Log.d("camera", "Set preview size to " + (int) frameSize.width + "x" + (int) frameSize.height);
					params.setPreviewSize((int) frameSize.width, (int) frameSize.height);
					params.setPictureSize((int) pictureSize.width, (int) pictureSize.height);

					if (!Build.MODEL.equals("GT-I9100")) {
						params.setRecordingHint(true);
					}

					List<String> FocusModes = params.getSupportedFocusModes();
					if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
						params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
					}

					camera.setParameters(params);
					params = camera.getParameters();

					mFrameWidth = params.getPreviewSize().width;
					mFrameHeight = params.getPreviewSize().height;

					if ((getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) && (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT))
						mScale = Math.min(((float) height) / mFrameHeight, ((float) width) / mFrameWidth);
					else
						mScale = 0;

					if (mFpsMeter != null) {
						mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
					}

					int size = mFrameWidth * mFrameHeight;
					size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
					mBuffer = new byte[size];
					camera.addCallbackBuffer(mBuffer);
					camera.setPreviewCallbackWithBuffer(this);

					mFrameChain = new Mat[2];
					mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);
					mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);

					AllocateCache();

					mCameraFrame = new OCVCameraView.JavaCameraFrame[2];
					mCameraFrame[0] = new OCVCameraView.JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
					mCameraFrame[1] = new OCVCameraView.JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);

					surfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
					camera.setPreviewTexture(surfaceTexture);

					/* Finally we are ready to start the preview */
					Log.d("camera", "startPreview");
					camera.startPreview();
				} else
					result = false;
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}

	protected void releaseCamera() {
		synchronized (this) {
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallback(null);

				camera.release();
			}
			camera = null;
			if (mFrameChain != null) {
				mFrameChain[0].release();
				mFrameChain[1].release();
			}
			if (mCameraFrame != null) {
				mCameraFrame[0].release();
				mCameraFrame[1].release();
			}
		}
	}

	private boolean mCameraFrameReady = false;

	@Override
	protected boolean connectCamera(int width, int height) {

		/* 1. We need to instantiate camera
		 * 2. We need to start thread which will be getting frames
		 */
		/* First step - initialize camera connection */
		Log.d(TAG, "Connecting to camera");
		if (!initializeCamera(width, height))
			return false;

		mCameraFrameReady = false;

		/* now we can start update thread */
		Log.d(TAG, "Starting processing thread");
		mStopThread = false;
		mThread = new Thread(new OCVCameraView.CameraWorker());
		mThread.start();

		return true;
	}

	@Override
	protected void disconnectCamera() {
		/* 1. We need to stop thread which updating the frames
		 * 2. Stop camera and release it
		 */
		Log.d(TAG, "Disconnecting from camera");
		try {
			mStopThread = true;
			Log.d(TAG, "Notify thread");
			synchronized (this) {
				this.notify();
			}
			Log.d(TAG, "Waiting for thread");
			if (mThread != null)
				mThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mThread = null;
		}

		/* Now release camera */
		releaseCamera();

		mCameraFrameReady = false;
	}

	@Override
	public void onPreviewFrame(byte[] frame, Camera arg1) {
		synchronized (this) {
			mFrameChain[mChainIdx].put(0, 0, frame);
			mCameraFrameReady = true;
			this.notify();
		}
		if (camera != null)
			camera.addCallbackBuffer(mBuffer);
	}

	public void takePicture() {
		if (camera == null) {
			Log.e("camera", "Ininitialized camera while attempting to take a picture. Aborting it.");
			return;
		}
		Log.i("camera", "Taking picture");
		// Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
		// Clear up buffers to avoid camera.takePicture to be stuck because of a memory issue
		camera.setPreviewCallback(null);

		// PictureCallback is implemented by the current class
		camera.takePicture(null, null, this);
	}

	public void setPhotoCallback(PhotoCallback photoCallback) {
		this.photoCallback = photoCallback;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.i("camera", "Picture was taken.");
		// The camera preview was automatically stopped. Start it again.
		this.camera.startPreview();
		this.camera.setPreviewCallback(this);

		if (photoCallback != null) {
			photoCallback.onPhotoTaken(data);
		}
	}

	private class JavaCameraFrame implements CvCameraViewFrame {
		@Override
		public Mat gray() {
			return mYuvFrameData.submat(0, mHeight, 0, mWidth);
		}

		@Override
		public Mat rgba() {
			Imgproc.cvtColor(mYuvFrameData, mRgba, Imgproc.COLOR_YUV2RGBA_NV21, 4);
			return mRgba;
		}

		JavaCameraFrame(Mat Yuv420sp, int width, int height) {
			super();
			mWidth = width;
			mHeight = height;
			mYuvFrameData = Yuv420sp;
			mRgba = new Mat();
		}

		public void release() {
			mRgba.release();
		}

		private Mat mYuvFrameData;
		private Mat mRgba;
		private int mWidth;
		private int mHeight;
	}

	private class CameraWorker implements Runnable {

		@Override
		public void run() {
			do {
				boolean hasFrame = false;
				synchronized (OCVCameraView.this) {
					try {
						while (!mCameraFrameReady && !mStopThread) {
							OCVCameraView.this.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (mCameraFrameReady) {
						mChainIdx = 1 - mChainIdx;
						mCameraFrameReady = false;
						hasFrame = true;
					}
				}

				if (!mStopThread && hasFrame) {
					if (!mFrameChain[1 - mChainIdx].empty())
						deliverAndDrawFrame(mCameraFrame[1 - mChainIdx]);
				}
			} while (!mStopThread);
			Log.d(TAG, "Finish processing thread");
		}
	}
}
