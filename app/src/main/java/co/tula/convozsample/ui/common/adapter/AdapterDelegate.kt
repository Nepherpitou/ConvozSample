package co.tula.convozsample.ui.common.adapter

import android.view.ViewGroup

interface AdapterDelegate<T> {
    fun isForViewType(items: List<T>, position: Int): Boolean
    fun createViewHolder(parent: ViewGroup): BaseHolder<T>
    fun bindViewHolder(items: List<T>, position: Int, holder: BaseHolder<T>)

    fun onViewAttachedToWindow(holder: BaseHolder<T>) {}
    fun onViewDetachedFromWindow(holder: BaseHolder<T>) {}
}
