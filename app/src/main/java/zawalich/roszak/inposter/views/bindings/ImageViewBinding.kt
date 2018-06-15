package zawalich.roszak.inposter.views.bindings

import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Transformation
import zawalich.roszak.inposter.R
import zawalich.roszak.inposter.imaging.InposterTransformation
import zawalich.roszak.inposter.model.InposterColor
import zawalich.roszak.inposter.model.InposterValue
import zawalich.roszak.inposter.views.utils.PicassoHandler


class BitmapTransformation(val color: InposterColor, val value: InposterValue?) : Transformation {
	val transformation: InposterTransformation

	init {
		transformation = InposterTransformation(color, value)
	}

	override fun key(): String {
		return color.toString() + " " + value.toString()
	}

	override fun transform(source: Bitmap?): Bitmap {
		return transformation.transform(source)
	}
}


@BindingAdapter("imageUrl", "inposterColor", "inposterValue")
fun loadImageFromUrl(view: ImageView,
					 imagePath: String?,
					 color: InposterColor?,
					 value: InposterValue?) {
	if (imagePath == null || color == null || value == null) {
		return
	}

	val parsedUri = Uri.parse(imagePath)
	PicassoHandler.getPicassoInstance(parsedUri)
			.load(parsedUri)
			.transform(BitmapTransformation(color, value))
			.fit()
			.centerInside()
			.error(R.drawable.ic_arrow_down_24dp)
			.placeholder(R.drawable.ic_arrow_down_24dp)
			.into(view)
}
