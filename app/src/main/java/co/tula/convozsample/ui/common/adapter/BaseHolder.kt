package co.tula.convozsample.ui.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    constructor(parent: ViewGroup, @LayoutRes layout: Int) :
            this(LayoutInflater.from(parent.context).inflate(layout, parent, false))

    open fun bindModel(item: T) {}
}
