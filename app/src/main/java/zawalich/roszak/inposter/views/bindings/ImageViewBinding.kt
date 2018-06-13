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
		val withTexts = drawTexts(colorized)
		return withTexts
	}

	private fun drawTexts(source: Bitmap): Bitmap {
		val result = Bitmap.createBitmap(source.width, source.height,
				Bitmap.Config.ARGB_8888);

		val paint = Paint()
		paint.color = getTextColor()// Text Color
		paint.textSize =70f// Text Size
		paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // Text Overlapping Pattern


		val canvas = Canvas(result)
		canvas.drawBitmap(source, 0f, 0f, paint)
		canvas.rotate(90f, source.width /2f, source.height /2f)
		canvas.drawText(value.text, source.width * (1f/3f), source.height * (1f/3f), paint)
		canvas.drawText(value.text2, source.width * (1f/3f), source.height * (2f/3f), paint)
		canvas.rotate(-90f,source.width /2f, source.height /2f)
		source.recycle()
		return result
	}

	private fun getTextColor(): Int {
		return when(color){
			InposterColor.Blue -> Color.YELLOW
			InposterColor.Purple -> Color.GREEN
			InposterColor.Green -> Color.BLUE
			InposterColor.Yellow -> Color.BLUE
		}
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
			.centerInside()
			.error(R.drawable.ic_arrow_down_24dp)
			.placeholder(R.drawable.ic_arrow_down_24dp)
			.into(view)
}
