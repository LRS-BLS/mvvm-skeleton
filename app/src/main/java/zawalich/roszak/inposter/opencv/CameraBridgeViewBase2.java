package zawalich.roszak.inposter.opencv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.android.FpsMeter;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import zawalich.roszak.inposter.R;

public abstract class CameraBridgeViewBase2 extends SurfaceView implements SurfaceHolder.Callback {

	private static final String TAG = "CameraBridge";
	private static final int MAX_UNSPECIFIED = -1;
	private static final int STOPPED = 0;
	private static final int STARTED = 1;
	private static final double MAX_RATIO_DEFORMATION = 0.05;
	private static final int MIN_PREVIEW_WIDTH = 640;

	private int mState = STOPPED;
	private Bitmap mCacheBitmap;
	private CameraBridgeViewBase2.CvCameraViewListener2 mListener;
	private boolean mSurfaceExist;
	private final Object mSyncObject = new Object();

	protected int mFrameWidth;
	protected int mFrameHeight;
	protected int mMaxHeight;
	protected int mMaxWidth;
	protected float mScale = 0;
	protected int mPreviewFormat = RGBA;
	protected boolean mEnabled;
	protected FpsMeter mFpsMeter = null;

	public static final int RGBA = 1;
	public static final int GRAY = 2;

	public CameraBridgeViewBase2(Context context, AttributeSet attrs) {
		super(context, attrs);

		int count = attrs.getAttributeCount();
		Log.d(TAG, "Attr count: " + count);

		TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.CameraBridgeViewBase2);
		if (styledAttrs.getBoolean(R.styleable.CameraBridgeViewBase2_show_fps, false)) {
			enableFpsMeter();
		}

		getHolder().addCallback(this);
		mMaxWidth = MAX_UNSPECIFIED;
		mMaxHeight = MAX_UNSPECIFIED;
		styledAttrs.recycle();
	}

	public interface CvCameraViewListener {
		/**
		 * This method is invoked when camera preview has started. After this method is invoked
		 * the frames will start to be delivered to client via the onCameraFrame() callback.
		 *
		 * @param width  -  the width of the frames that will be delivered
		 * @param height - the height of the frames that will be delivered
		 */
		void onCameraViewStarted(int width, int height);

		/**
		 * This method is invoked when camera preview has been stopped for some reason.
		 * No frames will be delivered via onCameraFrame() callback after this method is called.
		 */
		void onCameraViewStopped();

		/**
		 * This method is invoked when delivery of the frame needs to be done.
		 * The returned values - is a modified frame which needs to be displayed on the screen.
		 * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
		 */
		Mat onCameraFrame(Mat inputFrame);
	}

	public interface CvCameraViewListener2 {
		/**
		 * This method is invoked when camera preview has started. After this method is invoked
		 * the frames will start to be delivered to client via the onCameraFrame() callback.
		 *
		 * @param width  -  the width of the frames that will be delivered
		 * @param height - the height of the frames that will be delivered
		 */
		void onCameraViewStarted(int width, int height);

		/**
		 * This method is invoked when camera preview has been stopped for some reason.
		 * No frames will be delivered via onCameraFrame() callback after this method is called.
		 */
		void onCameraViewStopped();

		/**
		 * This method is invoked when delivery of the frame needs to be done.
		 * The returned values - is a modified frame which needs to be displayed on the screen.
		 * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
		 */
		Mat onCameraFrame(CameraBridgeViewBase2.CvCameraViewFrame inputFrame);
	}

	protected class CvCameraViewListenerAdapter implements CameraBridgeViewBase2.CvCameraViewListener2 {
		CvCameraViewListenerAdapter(CameraBridgeViewBase2.CvCameraViewListener oldStypeListener) {
			mOldStyleListener = oldStypeListener;
		}

		public void onCameraViewStarted(int width, int height) {
			mOldStyleListener.onCameraViewStarted(width, height);
		}

		public void onCameraViewStopped() {
			mOldStyleListener.onCameraViewStopped();
		}

		public Mat onCameraFrame(CameraBridgeViewBase2.CvCameraViewFrame inputFrame) {
			Mat result = null;
			switch (mPreviewFormat) {
				case RGBA:
					result = mOldStyleListener.onCameraFrame(inputFrame.rgba());
					break;
				case GRAY:
					result = mOldStyleListener.onCameraFrame(inputFrame.gray());
					break;
				default:
					Log.e(TAG, "Invalid frame format! Only RGBA and Gray Scale are supported!");
			}

			return result;
		}

		void setFrameFormat(int format) {
			mPreviewFormat = format;
		}

		private int mPreviewFormat = RGBA;
		private CameraBridgeViewBase2.CvCameraViewListener mOldStyleListener;
	}

	/**
	 * This class interface is abstract representation of single frame from camera for onCameraFrame callback
	 * Attention: Do not use objects, that represents this interface out of onCameraFrame callback!
	 */
	public interface CvCameraViewFrame {

		/**
		 * This method returns RGBA Mat with frame
		 */
		Mat rgba();

		/**
		 * This method returns single channel gray scale Mat with frame
		 */
		Mat gray();
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		Log.d(TAG, "call surfaceChanged event");
		synchronized (mSyncObject) {
			if (!mSurfaceExist) {
				mSurfaceExist = true;
				checkCurrentState();
			} else {
				/* Surface changed. We need to stop camera and restart with new parameters */
				/* Pretend that old surface has been destroyed */
				mSurfaceExist = false;
				checkCurrentState();
				/* Now use new surface. Say we have it now */
				mSurfaceExist = true;
				checkCurrentState();
			}
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		/* Do nothing. Wait until surfaceChanged delivered */
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		synchronized (mSyncObject) {
			mSurfaceExist = false;
			checkCurrentState();
		}
	}

	/**
	 * This method is provided for clients, so they can enable the camera connection.
	 * The actual onCameraViewStarted callback will be delivered only after both this method is called and surface is available
	 */
	public void enableView() {
		synchronized (mSyncObject) {
			mEnabled = true;
			checkCurrentState();
		}
	}

	/**
	 * This method is provided for clients, so they can disable camera connection and stop
	 * the delivery of frames even though the surface view itself is not destroyed and still stays on the scren
	 */
	public void disableView() {
		synchronized (mSyncObject) {
			mEnabled = false;
			checkCurrentState();
		}
	}

	/**
	 * This method enables label with fps value on the screen
	 */
	public void enableFpsMeter() {
		if (mFpsMeter == null) {
			mFpsMeter = new FpsMeter();
			mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
		}
	}

	public void disableFpsMeter() {
		mFpsMeter = null;
	}

	public void setCvCameraViewListener(CameraBridgeViewBase2.CvCameraViewListener2 listener) {
		mListener = listener;
	}

	/**
	 * This method sets the maximum size that camera frame is allowed to be. When selecting
	 * size - the biggest size which less or equal the size set will be selected.
	 * As an example - we set setMaxFrameSize(200,200) and we have 176x152 and 320x240 sizes. The
	 * preview frame will be selected with 176x152 size.
	 * This method is useful when need to restrict the size of preview frame for some reason (for example for video recording)
	 *
	 * @param maxWidth  - the maximum width allowed for camera frame.
	 * @param maxHeight - the maximum height allowed for camera frame
	 */
	public void setMaxFrameSize(int maxWidth, int maxHeight) {
		mMaxWidth = maxWidth;
		mMaxHeight = maxHeight;
	}

	public void setCaptureFormat(int format) {
		mPreviewFormat = format;
		if (mListener instanceof CameraBridgeViewBase2.CvCameraViewListenerAdapter) {
			CameraBridgeViewBase2.CvCameraViewListenerAdapter adapter = (CameraBridgeViewBase2.CvCameraViewListenerAdapter) mListener;
			adapter.setFrameFormat(mPreviewFormat);
		}
	}

	/**
	 * Called when mSyncObject lock is held
	 */
	private void checkCurrentState() {
		Log.d(TAG, "call checkCurrentState");
		int targetState;

		if (mEnabled && mSurfaceExist && getVisibility() == VISIBLE) {
			targetState = STARTED;
		} else {
			targetState = STOPPED;
		}

		if (targetState != mState) {
			/* The state change detected. Need to exit the current state and enter target state */
			processExitState(mState);
			mState = targetState;
			processEnterState(mState);
		}
	}

	private void processEnterState(int state) {
		Log.d(TAG, "call processEnterState: " + state);
		switch (state) {
			case STARTED:
				onEnterStartedState();
				if (mListener != null) {
					mListener.onCameraViewStarted(mFrameWidth, mFrameHeight);
				}
				break;
			case STOPPED:
				onEnterStoppedState();
				if (mListener != null) {
					mListener.onCameraViewStopped();
				}
				break;
		}
	}

	private void processExitState(int state) {
		Log.d(TAG, "call processExitState: " + state);
		switch (state) {
			case STARTED:
				onExitStartedState();
				break;
			case STOPPED:
				onExitStoppedState();
				break;
		}
	}

	private void onEnterStoppedState() {
		/* nothing to do */
	}

	private void onExitStoppedState() {
		/* nothing to do */
	}

	// NOTE: The order of bitmap constructor and camera connection is important for android 4.1.x
	// Bitmap must be constructed before surface
	private void onEnterStartedState() {
		Log.d(TAG, "call onEnterStartedState");
		/* Connect camera */
		if (!connectCamera(getWidth(), getHeight())) {
			AlertDialog ad = new AlertDialog.Builder(getContext()).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setMessage("It seems that you device does not support camera (or it is locked). Application will be closed.");
			ad.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", (dialog, which) -> {
				dialog.dismiss();
				((Activity) getContext()).finish();
			});
			ad.show();

		}
	}

	private void onExitStartedState() {
		disconnectCamera();
		if (mCacheBitmap != null) {
			mCacheBitmap.recycle();
		}
	}

	/**
	 * This method shall be called by the subclasses when they have valid
	 * object and want it to be delivered to external client (via callback) and
	 * then displayed on the screen.
	 *
	 * @param frame - the current frame to be delivered
	 */
	protected void deliverAndDrawFrame(CameraBridgeViewBase2.CvCameraViewFrame frame) {
		Mat modified;

		if (mListener != null) {
			modified = mListener.onCameraFrame(frame);
		} else {
			modified = frame.rgba();
		}

		boolean bmpValid = true;
		if (modified != null) {
			try {
				Utils.matToBitmap(modified, mCacheBitmap);
			} catch (Exception e) {
				Log.e(TAG, "Mat type: " + modified);
				Log.e(TAG, "Bitmap type: " + mCacheBitmap.getWidth() + "*" + mCacheBitmap.getHeight());
				Log.e(TAG, "Utils.matToBitmap() throws an exception: " + e.getMessage());
				bmpValid = false;
			}
		}

		if (bmpValid && mCacheBitmap != null) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null) {
				canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
//				if (BuildConfig.DEBUG) {
//					Log.d(TAG, "mStretch value: " + mScale);
//				}

				if (mScale != 0) {
					if (canvas.getWidth() > canvas.getHeight()) {
						canvas.drawBitmap(mCacheBitmap, new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
								new Rect((int) ((canvas.getWidth() - mScale * mCacheBitmap.getWidth()) / 2),
										(int) ((canvas.getHeight() - mScale * mCacheBitmap.getHeight()) / 2),
										(int) ((canvas.getWidth() - mScale * mCacheBitmap.getWidth()) / 2 + mScale * mCacheBitmap.getWidth()),
										(int) ((canvas.getHeight() - mScale * mCacheBitmap.getHeight()) / 2 + mScale * mCacheBitmap.getHeight())), null);
					} else {
						canvas.drawBitmap(mCacheBitmap, rotateMe(canvas, mCacheBitmap), null);
					}
				} else {
					canvas.drawBitmap(mCacheBitmap, new Rect(0, 0, mCacheBitmap.getWidth(), mCacheBitmap.getHeight()),
							new Rect((canvas.getWidth() - mCacheBitmap.getWidth()) / 2,
									(canvas.getHeight() - mCacheBitmap.getHeight()) / 2,
									(canvas.getWidth() - mCacheBitmap.getWidth()) / 2 + mCacheBitmap.getWidth(),
									(canvas.getHeight() - mCacheBitmap.getHeight()) / 2 + mCacheBitmap.getHeight()), null);
				}

				if (mFpsMeter != null) {
					mFpsMeter.measure();
					mFpsMeter.draw(canvas, 20, 30);
				}
				getHolder().unlockCanvasAndPost(canvas);
			}
		}
	}

	private Matrix rotateMe(Canvas canvas, Bitmap bm) {
		Matrix mtx = new Matrix();
		float scale = (float) canvas.getWidth() / (float) bm.getHeight();
		mtx.preTranslate((canvas.getWidth() - bm.getWidth()) / 2, (canvas.getHeight() - bm.getHeight()) / 2);
		mtx.postRotate(270, canvas.getWidth() / 2, canvas.getHeight() / 2);
		mtx.postScale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
		return mtx;
	}

	/**
	 * This method is invoked shall perform concrete operation to initialize the camera.
	 * CONTRACT: as a result of this method variables mFrameWidth and mFrameHeight MUST be
	 * initialized with the size of the Camera frames that will be delivered to external processor.
	 *
	 * @param width  - the width of this SurfaceView
	 * @param height - the height of this SurfaceView
	 */
	protected abstract boolean connectCamera(int width, int height);

	/**
	 * Disconnects and release the particular camera object being connected to this surface view.
	 * Called when syncObject lock is held
	 */
	protected abstract void disconnectCamera();

	// NOTE: On Android 4.1.x the function must be called before SurfaceTexture constructor!
	protected void AllocateCache() {
		mCacheBitmap = Bitmap.createBitmap(mFrameWidth, mFrameHeight, Bitmap.Config.ARGB_8888);
	}

	public interface ListItemAccessor {
		int getWidth(Object obj);

		int getHeight(Object obj);
	}

	/**
	 * This helper method can be called by subclasses to select camera preview size.
	 * It goes over the list of the supported preview sizes and selects the maximum one which
	 * fits both values set via setMaxFrameSize() and surface frame allocated for this view
	 *
	 * @param supportedSizes sizes
	 * @param surfaceWidth   width
	 * @param surfaceHeight  height
	 * @return optimal frame size
	 */
	@SuppressWarnings("SuspiciousNameCombination")
	protected Size calculateCameraFrameSize(List<?> supportedSizes, ListItemAccessor accessor, Size pSize, int surfaceWidth, int surfaceHeight) {
		Object bestSize = null;
		Object bestSizeSecondChoice = null;
		double pictureAspectRatioFactor = pSize.width / pSize.height;
		long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long availableMemory = Runtime.getRuntime().maxMemory() - used;
		Collections.sort(supportedSizes, (Comparator<Object>) (o1, o2) -> accessor.getWidth(o1) < accessor.getWidth(o2) ? -1 : 1);

		for (Object currentSize : supportedSizes) {
			int newArea = accessor.getWidth(currentSize) * accessor.getHeight(currentSize);

			if (accessor.getWidth(currentSize) < MIN_PREVIEW_WIDTH) {
				continue;
			}

			long neededMemory = newArea * 4 * 6; // newArea * 4 Bytes/pixel * 6 needed copies of the bitmap (for safety :) )
			boolean isDesiredRatio = false;
			boolean isBetterSize = (bestSize == null || accessor.getWidth(currentSize) > accessor.getWidth(bestSize));
			boolean isSafe = neededMemory < availableMemory;
			boolean isAllowedMaxSize = (accessor.getWidth(currentSize) <= surfaceWidth && accessor.getHeight(currentSize) <= surfaceHeight);
			double currentFactor = (double) accessor.getWidth(currentSize) / (double) accessor.getHeight(currentSize);

			if (currentFactor > pictureAspectRatioFactor - MAX_RATIO_DEFORMATION && currentFactor < pictureAspectRatioFactor + MAX_RATIO_DEFORMATION) {
				isDesiredRatio = true;
			}

			if (isDesiredRatio && isBetterSize && isSafe && isAllowedMaxSize) {
				bestSize = currentSize;
			}

			if (isDesiredRatio && isSafe && bestSizeSecondChoice == null) {
				bestSizeSecondChoice = currentSize;
			}
		}

		if (bestSize == null && bestSizeSecondChoice == null) {
			return new Size(accessor.getWidth(supportedSizes.get(0)), accessor.getHeight(supportedSizes.get(0)));
		}

		if (bestSize == null) {
			return new Size(accessor.getWidth(bestSizeSecondChoice), accessor.getHeight(bestSizeSecondChoice));
		}
		return new Size(accessor.getWidth(bestSize), accessor.getHeight(bestSize));
	}

	protected Size calculatePictureSize(List<?> supportedSizes, ListItemAccessor accessor) {
		Object bestSize = null;
		Collections.sort(supportedSizes, (Comparator<Object>) (o1, o2) -> accessor.getWidth(o1) < accessor.getWidth(o2) ? -1 : 1);

		for (Object currentSize : supportedSizes) {
			boolean isDesiredRatio = accessor.getWidth(currentSize) > accessor.getHeight(currentSize);

			if (isDesiredRatio) {
				bestSize = currentSize;
			}
		}

		if (bestSize == null) {
			return new Size(accessor.getWidth(supportedSizes.get(0)), accessor.getHeight(supportedSizes.get(0)));
		}

		return new Size(accessor.getWidth(bestSize), accessor.getHeight(bestSize));
	}
}
