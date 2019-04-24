package co.tula.convozsample.ui.common.adapter


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DelegateAdapter<T>(vararg delegate: AdapterDelegate<T>) : RecyclerView.Adapter<BaseHolder<T>>() {

    val items: MutableList<T> = mutableListOf()
    private val manager = AdapterDelegateManager<T>()

    init {
        manager.delegates.addAll(delegate)
    }

    override fun onViewAttachedToWindow(holder: BaseHolder<T>) {
        super.onViewAttachedToWindow(holder)
        manager.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: BaseHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        manager.onViewDetachedFromWindow(holder)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseHolder<T>, position: Int) {
        manager.bindViewHolder(items, position, holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<T> {
        return manager.createViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int = manager.getItemViewType(items, position)
}
