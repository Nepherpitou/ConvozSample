package co.tula.convozsample.ui.common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class MVIController<State, Intent, Render, Action>(initialState: State) {

    protected val supervisor = SupervisorJob()
    protected open val scope = CoroutineScope(Dispatchers.Default + supervisor)
    protected var subscription: Job? = null

    protected open val renderChannel = Channel<List<Render>>(Channel.UNLIMITED)
    protected open val actionChannel = Channel<Action>(Channel.UNLIMITED)
    protected open val intentChannel = Channel<Intent>(Channel.UNLIMITED)

    val renders: ReceiveChannel<List<Render>> get() = renderChannel
    val actions: ReceiveChannel<Action> get() = actionChannel

    private val stateActor = scope.stateActor(initialState)

    protected suspend fun changeState(block: suspend (State) -> State) {
        withState { old ->
            stateActor.send(StateActorCommand.Updater(block))
            withState { new ->
                sendRenders(renderDiff(old, new))
            }
        }
    }

    protected suspend fun replaceState(block: suspend (State) -> State) {
        stateActor.send(StateActorCommand.Updater(block))
    }

    protected suspend fun retrieveState(): State {
        return CompletableDeferred<State>().also { stateActor.send(StateActorCommand.Getter(it)) }.await()
    }

    protected suspend fun <T> withState(block: suspend (State) -> T): T {
        return block(retrieveState())
    }

    protected open fun sendRenders(renders: List<Render>) {
        if (renders.isEmpty()) return
        renderChannel.offer(renders)
    }

    protected open fun sendAction(action: Action) {
        actionChannel.offer(action)
    }

    open fun sendIntent(intent: Intent) {
        intentChannel.offer(intent)
    }

    protected abstract suspend fun onIntent(intent: Intent)
    protected abstract fun renderDiff(old: State, new: State): List<Render>
    protected abstract fun renderState(state: State): List<Render>

    protected fun CoroutineScope.launchIntentConsumer() {
        launch { for (intent in intentChannel) onIntent(intent) }
    }

    fun start() {
        if (subscription != null) return
        subscription = scope.launch { asyncStart(this) }
    }

    open suspend fun asyncStart(scope: CoroutineScope) {
        scope.launchIntentConsumer()
    }

    open fun stop() {
        supervisor.cancelChildren()
        subscription?.cancel()
        subscription = null
    }
}
