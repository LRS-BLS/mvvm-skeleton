package zawalich.roszak.inposter.opencv;

import java.lang.ref.WeakReference;

import zawalich.roszak.inposter.model.PhotoQuality;

public abstract class BaseCvCameraViewListener2 implements CameraBridgeViewBase2.CvCameraViewListener2 {

	public interface PhotoQualityListener {
		void photoQualityChanged(PhotoQuality pq);
	}

	private WeakReference<PhotoQualityListener> listener;

	public BaseCvCameraViewListener2() {
		super();
	}

	public void setPhotoQualityListener(PhotoQualityListener listener) {
		this.listener = new WeakReference<>(listener);
	}

	protected void setGoodImageQuality() {
		if (listener != null && listener.get() != null) {
			listener.get().photoQualityChanged(PhotoQuality.GOOD);
		}
	}

	protected void setBadImageQuality() {
		if (listener != null && listener.get() != null) {
			listener.get().photoQualityChanged(PhotoQuality.BAD);
		}
	}
}
