package co.tula.convozsample.ui.main

import co.tula.convozsample.data.Repository
import co.tula.convozsample.extensions.Err
import co.tula.convozsample.extensions.Ok
import co.tula.convozsample.ui.common.mvi.LifecycleStep
import co.tula.convozsample.ui.common.mvi.MVILifecycleController
import kotlinx.coroutines.launch

class MainController(
    private val repository: Repository
) : MVILifecycleController<MainState, MainIntent, MainRender, MainAction>(MainState()) {

    override suspend fun onLifecycle(step: LifecycleStep) {
        super.onLifecycle(step)
    }

    override suspend fun onIntent(intent: MainIntent) = when (intent) {
        MainIntent.Search -> onSearch()
        is MainIntent.Query -> onQuery(intent.value)
        MainIntent.Paginate -> onPaginate()
    }

    override fun renderDiff(old: MainState, new: MainState): List<MainRender> = listOfNotNull(
        new.images.takeIf { it != old.images }?.let { MainRender.RImages(it) },
        if (new.loadJob != old.loadJob) MainRender.RLoader(new.loadJob != null) else null
    )

    override fun renderState(state: MainState): List<MainRender> = listOf(
        MainRender.RImages(state.images),
        MainRender.RLoader(state.loadJob != null)
    )

    private suspend fun onQuery(query: String) = changeState { it.copy(query = query) }

    private suspend fun onSearch() = withState { state ->
        state.loadJob?.cancel()
        val job = scope.launch {
            when (val r = repository.search(state.query)) {
                is Ok -> changeState { it.copy(images = r.value, loadJob = null) }
                is Err -> {
                    changeState { it.copy(images = emptyList(), loadJob = null) }
                    sendAction(MainAction.Error(r.error))
                }
            }
        }
        changeState { it.copy(loadJob = job) }
    }

    private suspend fun onPaginate() = withState { state ->
        if (state.loadJob == null) {
            val job = scope.launch {
                when (val r = repository.search(state.query, state.images.size)) {
                    is Ok -> changeState { it.copy(images = it.images + r.value, loadJob = null) }
                    is Err -> {
                        changeState { it.copy(loadJob = null) }
                        sendAction(MainAction.Error(r.error))
                    }
                }
            }
            changeState { it.copy(loadJob = job) }
        }
    }

}