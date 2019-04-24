package co.tula.convozsample.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.tula.convozsample.R
import co.tula.convozsample.data.GifObject
import co.tula.convozsample.data.ImageLoader
import co.tula.convozsample.ui.common.adapter.AdapterDelegate
import co.tula.convozsample.ui.common.adapter.BaseHolder
import kotlinx.android.synthetic.main.item_gif.view.*

sealed class ListElement {
    data class Image(val gif: GifObject) : ListElement()
    object Loader : ListElement()
}

class ImageDelegate(private val imageLoader: ImageLoader) : AdapterDelegate<ListElement> {
    override fun isForViewType(items: List<ListElement>, position: Int): Boolean = items[position] is ListElement.Image

    override fun createViewHolder(parent: ViewGroup): BaseHolder<ListElement> = ImageHolder(
        imageLoader,
        LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
    )

    override fun bindViewHolder(items: List<ListElement>, position: Int, holder: BaseHolder<ListElement>) {
        holder.bindModel(items[position])
    }

}

class ImageHolder(
    private val imageLoader: ImageLoader,
    view: View
) : BaseHolder<ListElement>(view) {

    override fun bindModel(item: ListElement) {
        super.bindModel(item)
        displayGif((item as ListElement.Image).gif)
    }

    private fun displayGif(gif: GifObject) {
        imageLoader.displayGif(itemView.image, gif)
        itemView.text.text = gif.url
    }
}

class LoaderDelegate : AdapterDelegate<ListElement> {
    override fun isForViewType(items: List<ListElement>, position: Int) = items[position] is ListElement.Loader

    override fun createViewHolder(parent: ViewGroup): BaseHolder<ListElement> {
        return BaseHolder(parent, R.layout.item_loader)
    }

    override fun bindViewHolder(items: List<ListElement>, position: Int, holder: BaseHolder<ListElement>) {

    }

}