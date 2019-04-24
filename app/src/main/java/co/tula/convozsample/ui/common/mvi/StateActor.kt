package co.tula.convozsample.ui.common.mvi

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor

fun <T> CoroutineScope.stateActor(initialState: T): SendChannel<StateActorCommand<T>> {
    var state = initialState
    return actor {
        for (command in channel) {
            when (command) {
                is StateActorCommand.Getter -> command.deferred.complete(state)
                is StateActorCommand.Updater -> state = command.update(state)
            }
        }
    }
}

sealed class StateActorCommand<T> {
    class Getter<T>(val deferred: CompletableDeferred<T>) : StateActorCommand<T>()
    class Updater<T>(val update: suspend (T) -> T) : StateActorCommand<T>()
}
