package zawalich.roszak.inposter.views.bindings

import android.databinding.BindingAdapter
import android.graphics.*
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Transformation
import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue
import zawalich.roszak.inposter.views.utils.PicassoHandler


class BitmapTransformation(val color: InposterColor ,val value : InposterValue) : Transformation{
	override fun key(): String {
		return color.toString() + " " + value.toString()
	}

	override fun transform(source: Bitmap?): Bitmap {
		val dessaturated = dessaturate(source!!)
		val colorized = colorize(dessaturated)
		return colorized
	}

	fun colorize(source: Bitmap) : Bitmap{
		val result = Bitmap.createBitmap(source.width, source.height,
				Bitmap.Config.ARGB_8888);
		val b = color.hex and 0xFF
		val g = color.hex.shr(8) and 0xFF
		val r = color.hex.shr(16) and 0xFF

		val src = floatArrayOf(
				r/255f, 0f, 	0f, 	0f, 0f,
				0f, 	g/255f, 0f, 	0f, 0f,
				0f, 	0f, 	b/255f, 0f, 0f,
				0f, 	0f, 	0f, 	1f, 0f)
		val cm = ColorMatrix(src)

		val filter = ColorMatrixColorFilter(cm)
		val paint = Paint()
		paint.colorFilter = filter

		val canvas = Canvas(result)
		canvas.drawBitmap(source, 0f, 0f, paint)
		source.recycle()
		return result
	}

	fun dessaturate(source: Bitmap): Bitmap {
		val result = Bitmap.createBitmap(source.width, source.height,
				Bitmap.Config.ARGB_8888);

		val cm = ColorMatrix()
		cm.setSaturation(0f)
		val filter = ColorMatrixColorFilter(cm)
		val paint = Paint()
		paint.colorFilter = filter

		val canvas = Canvas(result)
		canvas.drawBitmap(source, 0f, 0f, paint)
		source.recycle()
		return result
	}

}

@BindingAdapter("imageUrl", "inposterColor", "inposterValue")
fun loadImageFromUrl(view: ImageView,
					 imagePath: String?,
					 color: InposterColor?,
					 value: InposterValue?) {
	if(imagePath == null || color == null || value == null ){
		return
	}

	val parsedUri = Uri.parse(imagePath)
	PicassoHandler.getPicassoInstance(parsedUri)
			.load(parsedUri)
			.transform(BitmapTransformation(color,value))
			.fit()
			.centerCrop()
			.error(R.drawable.ic_arrow_down_24dp)
			.placeholder(R.drawable.ic_arrow_down_24dp)
			.into(view)
}
