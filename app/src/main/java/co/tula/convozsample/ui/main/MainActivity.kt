package co.tula.convozsample.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.tula.convozsample.R
import co.tula.convozsample.data.GifObject
import co.tula.convozsample.data.ImageLoader
import co.tula.convozsample.data.ImageLoaderImpl
import co.tula.convozsample.data.RepositoryImpl
import co.tula.convozsample.extensions.textWatcher
import co.tula.convozsample.ui.common.adapter.DelegateAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val supervisor = SupervisorJob()
    private val uiScope = CoroutineScope(supervisor + Dispatchers.Main)

    private val controller = MainController(RepositoryImpl())
    private val imageLoader: ImageLoader = ImageLoaderImpl(this)
    private val adapter = DelegateAdapter(
        ImageDelegate(imageLoader),
        LoaderDelegate()
    )

    private val queryWatcher = textWatcher { controller.sendIntent(MainIntent.Query(it)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller.start()
        uiScope.launch {
            launch { for (renders in controller.renders) renders.forEach { render(it) } }
            launch { for (action in controller.actions) processAction(action) }
        }

        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler.adapter = adapter
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisible = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastVisible != RecyclerView.NO_POSITION && lastVisible > adapter.items.size - 3) {
                    controller.sendIntent(MainIntent.Paginate)
                }
            }
        })
        query.addTextChangedListener(queryWatcher)
        search.setOnClickListener { controller.sendIntent(MainIntent.Search) }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.stop()
        supervisor.cancelChildren()
    }

    private fun render(r: MainRender) = when (r) {
        is MainRender.RImages -> renderImages(r.items)
        is MainRender.RLoader -> renderLoader(r.show)
    }

    private fun processAction(action: MainAction) = when (action) {
        is MainAction.Error -> Toast.makeText(this, action.cause.message, Toast.LENGTH_LONG).show()
    }

    //todo: Use DiffUtil
    private fun renderImages(images: List<GifObject>) {
        adapter.items.clear()
        adapter.items.addAll(images.map { ListElement.Image(it) })
        adapter.notifyDataSetChanged()
    }

    private fun renderLoader(show: Boolean) {
        val pos = adapter.items.indexOfFirst { it is ListElement.Loader }
        when {
            pos < 0 && show -> {
                adapter.notifyItemInserted(adapter.items.size)
                adapter.items.add(ListElement.Loader)
            }
            pos >= 0 && !show -> {
                adapter.items.removeAt(pos)
                adapter.notifyItemRemoved(pos)
            }
        }
    }
}
