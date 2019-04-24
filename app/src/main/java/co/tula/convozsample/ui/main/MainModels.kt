package co.tula.convozsample.ui.main

import co.tula.convozsample.data.GifObject
import kotlinx.coroutines.Job

data class MainState(
    val images: List<GifObject> = emptyList(),
    val loadJob: Job? = null,
    val query: String = ""
)

sealed class MainIntent {
    data class Query(val value: String) : MainIntent()
    object Search : MainIntent()
    object Paginate : MainIntent()
}

sealed class MainRender {
    data class RImages(val items: List<GifObject>) : MainRender()
    data class RLoader(val show: Boolean) : MainRender()
}

sealed class MainAction {
    data class Error(val cause: Exception) : MainAction()
}