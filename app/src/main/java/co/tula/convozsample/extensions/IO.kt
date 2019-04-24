package co.tula.convozsample.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class IO<out V, out E> {

    companion object {
        inline fun <V> of(action: () -> V): IO<V, Exception> {
            return try {
                Ok(action())
            } catch (ex: Exception) {
                Err(ex)
            } catch (th: Throwable) {
                Err(RuntimeException(th))
            }
        }
    }
}

data class Ok<out V>(val value: V) : IO<V, Nothing>()
data class Err<out E>(val error: E) : IO<Nothing, E>()

suspend fun <T> io(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T
) = withContext(dispatcher) { IO.of { block() } }
