package zawalich.roszak.inposter.model

import android.Manifest
import android.util.Log

enum class Permission(val value: String) {
	CAMERA(Manifest.permission.CAMERA),
	FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
	COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION);

	companion object {
		fun get(value: String): Permission {
			Log.d("Permission", "Getting $value permission")
			return enumValues<Permission>().first { permission -> permission.value == value }
		}
	}

	override fun toString(): String {
		return value
	}
}