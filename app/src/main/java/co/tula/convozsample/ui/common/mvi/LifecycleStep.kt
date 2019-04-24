package co.tula.convozsample.ui.common.mvi

sealed class LifecycleStep {
    object ViewCreated : LifecycleStep()
    object ViewDestroyed : LifecycleStep()
    object OnCreated : LifecycleStep()
    object OnDestroyed : LifecycleStep()
    object OnResume : LifecycleStep()
    object OnPause : LifecycleStep()
}
