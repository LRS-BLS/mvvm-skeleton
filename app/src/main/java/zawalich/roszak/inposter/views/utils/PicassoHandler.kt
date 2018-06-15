package zawalich.roszak.inposter.views.utils

import android.graphics.*
import android.net.Uri
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import com.squareup.picasso.Transformation
import zawalich.roszak.inposter.InPosterApplication
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue



object PicassoHandler {
	const val BYTE_ARRAY = "byte_array"
	const val NOT_ENCRYPTED = "not_encrypted"
	val map = HashMap<String, Picasso>()

	object byteRequestHandler: RequestHandler() {

		val arrayMap = HashMap<Uri, ByteArray?>()

		override fun canHandleRequest(request: Request?): Boolean {
			return arrayMap.containsKey(request?.uri)
		}

		override fun load(request: Request?, p1: Int): Result {
			val bmp = BitmapFactory.decodeByteArray(arrayMap[request?.uri], 0, arrayMap[request?.uri]!!.size)
			arrayMap.remove(request?.uri)
			return Result(bmp, Picasso.LoadedFrom.MEMORY)
		}
	}

	/**
	 * Provides proper picasso instance depends on uri scheme
	 */
	fun getPicassoInstance(uri: Uri): Picasso {
		when (uri.scheme) {
			"file" -> return getPicassoInstanceNotEncrypted()
		}
		return getPicassoInstanceNotEncrypted()
	}

	private fun getPicassoInstanceNotEncrypted(): Picasso {
		var picasso = map[NOT_ENCRYPTED]
		if (picasso == null) {
			picasso = Picasso.Builder(InPosterApplication.instance).build()
			map.put(NOT_ENCRYPTED, picasso)
		}

		return picasso!!
	}

	/**
	 * Provides proper picasso instance for byte array handling
	 */
	fun getPicassoInstance(uri: Uri, array: ByteArray?): Picasso {
		var picasso = map[BYTE_ARRAY]
		if (picasso == null) {
			picasso = Picasso.Builder(InPosterApplication.instance).addRequestHandler(byteRequestHandler).build()
			map.put(BYTE_ARRAY, picasso)
		}

		byteRequestHandler.arrayMap.put(uri, array)

		return picasso!!
	}
}