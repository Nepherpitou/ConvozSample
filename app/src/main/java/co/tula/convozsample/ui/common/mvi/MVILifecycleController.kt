package co.tula.convozsample.ui.common.mvi

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

abstract class MVILifecycleController<State, Intent, Render, Action>(
    initialState: State
) : MVIController<State, Intent, Render, Action>(initialState) {

    protected val lifecycleChannel = Channel<LifecycleStep>(Channel.UNLIMITED)
    protected val lcSupervisor = SupervisorJob()
    protected val bindedScope = CoroutineScope(Dispatchers.IO + lcSupervisor)

    protected fun CoroutineScope.launchLifecycleConsumer() {
        launch { for (step in lifecycleChannel) onLifecycle(step) }
    }

    override suspend fun asyncStart(scope: CoroutineScope) {
        super.asyncStart(scope)
        bindedScope.launchLifecycleConsumer()
    }

    override fun stop() {
        super.stop()
        lcSupervisor.cancelChildren()
    }

    open suspend fun onLifecycle(step: LifecycleStep) {
        renderStateIfCreated(step)
    }

    protected suspend fun renderStateIfCreated(step: LifecycleStep) {
        when (step) {
            is LifecycleStep.ViewCreated -> withState { sendRenders(renderState(it)) }
        }
    }

    fun sendLifecycle(step: LifecycleStep) {
        lifecycleChannel.offer(step)
    }

}
