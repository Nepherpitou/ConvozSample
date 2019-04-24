package co.tula.convozsample.ui.common

import kotlinx.coroutines.Job

sealed class LoadModel<T> {
    class Promised<T> : LoadModel<T>()
    data class Load<T>(val job: Job) : LoadModel<T>()
    data class Model<T>(val value: T) : LoadModel<T>()
    data class Error<T>(val error: Exception) : LoadModel<T>()
}
