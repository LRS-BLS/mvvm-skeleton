package zawalich.roszak.inposter.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import zawalich.roszak.inposter.extensions.app
import zawalich.roszak.inposter.extensions.dagger
import zawalich.roszak.inposter.model.Permission
import zawalich.roszak.inposter.model.PhotoQuality
import zawalich.roszak.inposter.navigation.ContextModels.PreviewContextModel
import zawalich.roszak.inposter.navigation.ContextModels.RationaleDialogContextModel
import zawalich.roszak.inposter.navigation.NavigationAware
import zawalich.roszak.inposter.navigation.NavigationService
import zawalich.roszak.inposter.navigation.ScreenContextEnum
import zawalich.roszak.inposter.navigation.ScreenRegion
import zawalich.roszak.inposter.repositories.interfaces.PermissionsRepository
import zawalich.roszak.inposter.utils.AsyncUtils
import java.io.File
import java.io.IOException
import javax.inject.Inject

class CameraScreenViewModel() : BaseViewModel(), NavigationAware {

	@Inject
	lateinit var navigationService: NavigationService
	@Inject
	lateinit var permissionsRepository: PermissionsRepository
	val buttonEnabled = ObservableBoolean(false)
	private val isViewActive = BehaviorSubject.createDefault<Boolean>(false)
	private val isRationaleVisible = BehaviorSubject.createDefault<Boolean>(false)
	private var initOCV = MutableLiveData<Boolean>()
	private val takePicture = MutableLiveData<Long>()
	private var permissionDispose: Disposable? = null
	private var permissionRequestDispose: Disposable? = null
	private var permissionHandlingDispose: Disposable? = null

	init {
		dagger().inject(this)
	}

	override fun onNavigatedTo() {
		permissionRequestDispose = Observable
				.combineLatest<Boolean, Boolean, Pair<Boolean, Boolean>>(
						isViewActive,
						isRationaleVisible,
						BiFunction { isViewActive, isRationaleVisible -> Pair(isViewActive, isRationaleVisible) }
				)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe { (isViewActive, isRationaleVisible) ->
					if (isViewActive && !isRationaleVisible) {
						obtainPermission(Permission.CAMERA)
					}
				}

		permissionHandlingDispose = permissionsRepository.observe(Permission.CAMERA)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeBy(onNext = { isGranted -> initOCV.value = isGranted })
	}

	override fun onNavigatedFrom() {
		permissionDispose?.dispose()
		permissionRequestDispose?.dispose()
		permissionHandlingDispose?.dispose()
	}

	fun goToSample() {
		navigationService.navigate(
				ScreenRegion.Main,
				ScreenContextEnum.Sample()
		)
	}

	fun goToPreview() {
		takePicture.value = System.currentTimeMillis()
	}

	fun setViewActive(isViewActive: Boolean) {
		this.isViewActive.onNext(isViewActive)
	}

	fun setPhotoQuality(photoQuality: PhotoQuality) {
		when (photoQuality) {
			PhotoQuality.BAD -> buttonEnabled.set(false)
			PhotoQuality.GOOD -> buttonEnabled.set(true)
		}
	}

	fun pictureTaken(data: ByteArray) {
		var path: String? = null
		AsyncUtils.executeAsync(
				{ path = saveFile(data) },
				{
					navigationService.navigate(
							ScreenRegion.Main,
							ScreenContextEnum.Preview(PreviewContextModel(path!!))
					)
		})
	}

	private fun saveFile(data: ByteArray): String {
		var img: Bitmap? = null
		val path = app().cacheDir.path + java.io.File.separator + System.currentTimeMillis() + ".jpg"

		try {
			val file = File(path)
			img = BitmapFactory.decodeByteArray(data, 0, data.count())
			img.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())

		} catch (e: IOException) {
			e.message?.let {
				Log.e("I/O", e.message)
			}
		} finally {
			img?.recycle()
		}
		Log.d("image", path)
		return "file://${path}"
	}

	fun getInitOCV(): LiveData<Boolean> {
		return initOCV
	}

	fun getTakePicture(): LiveData<Long> {
		return takePicture
	}

	private fun obtainPermission(permission: Permission) {
		Log.e("CameraScreenViewModel", "obtaining permission")
		permissionDispose?.dispose()
		permissionDispose = null

		permissionDispose = permissionsRepository.requestEach(permission)
				.observeOn(Schedulers.single())
				.subscribeBy(
						onNext = { (_, status) ->
							Log.e("CameraScreenViewModel", "permission status: $status")
							if (status == null) {
								showRationaleDialog()
							}
						}
				)
	}

	private fun showRationaleDialog() {
		isRationaleVisible.onNext(true)
		navigationService.navigate(
				ScreenRegion.Dialog,
				ScreenContextEnum.RationaleDialog(RationaleDialogContextModel(
						{ isRationaleVisible.onNext(false) }, // navigation go back invoke permission request
						{
							permissionsRepository.ignoreRequest(Permission.CAMERA)
							isRationaleVisible.onNext(false)
						}
				))
		)
	}
}