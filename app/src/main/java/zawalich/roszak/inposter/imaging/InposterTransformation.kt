package zawalich.roszak.inposter.imaging

import android.graphics.*
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue


class InposterTransformation(val color: InposterColor, val value: InposterValue?) {
	fun transform(source: Bitmap?): Bitmap {
		val dessaturated = dessaturate(source!!)
		val colorized = colorize(dessaturated)
		if (value != null) {
			return drawTexts(colorized)
		}
		return colorized
	}

	private fun drawTexts(source: Bitmap): Bitmap {
		if(value == null) {
			return source
		}

		val result = Bitmap.createBitmap(source.width, source.height,
				Bitmap.Config.ARGB_8888);

		val paint = Paint()
		paint.color = getTextColor()// Text Color
		paint.textSize = 70f// Text Size
		paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER) // Text Overlapping Pattern


		val canvas = Canvas(result)
		canvas.drawBitmap(source, 0f, 0f, paint)
		canvas.rotate(90f, source.width / 2f, source.height / 2f)
		canvas.drawText(value.text, source.width * (1f / 3f), source.height * (1f / 5f), paint)
		canvas.drawText(value.text2, source.width * (1f / 3f), source.height * (4f / 5f), paint)
		canvas.rotate(-90f, source.width / 2f, source.height / 2f)
		source.recycle()
		return result
	}

	private fun getTextColor(): Int {
		return when (color) {
			InposterColor.Blue -> Color.YELLOW
			InposterColor.Purple -> Color.GREEN
			InposterColor.Green -> Color.BLUE
			InposterColor.Yellow -> Color.BLUE
		}
	}

	private fun colorize(source: Bitmap): Bitmap {
		val result = Bitmap.createBitmap(source.width, source.height,
				Bitmap.Config.ARGB_8888);
		val b = color.hex and 0xFF
		val g = color.hex.shr(8) and 0xFF
		val r = color.hex.shr(16) and 0xFF

		val src = floatArrayOf(
				r / 255f, 0f, 0f, 0f, 0f,
				0f, g / 255f, 0f, 0f, 0f,
				0f, 0f, b / 255f, 0f, 0f,
				0f, 0f, 0f, 1f, 0f)
		val cm = ColorMatrix(src)

		val filter = ColorMatrixColorFilter(cm)
		val paint = Paint()
		paint.colorFilter = filter

		val canvas = Canvas(result)
		canvas.drawBitmap(source, 0f, 0f, paint)
		source.recycle()
		return result
	}

	private fun dessaturate(source: Bitmap): Bitmap {
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