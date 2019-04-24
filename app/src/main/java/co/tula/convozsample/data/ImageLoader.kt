package co.tula.convozsample.data

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.Glide

interface ImageLoader {
    fun displayGif(imageView: ImageView, gif: GifObject)
}

class ImageLoaderImpl(private val activity: Activity) : ImageLoader {

    override fun displayGif(imageView: ImageView, gif: GifObject) {
        Glide.with(activity).load(gif.images.fixedHeight.url).into(imageView)
    }

}