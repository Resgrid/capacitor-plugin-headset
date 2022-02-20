package com.resgrid.plugins.headset

import io.reactivex.Observable

interface AccessoryManager {
    val connected: Boolean
    val connectedObservable: Observable<Boolean>
    val buttonEventsObservable: Observable<PttButtonEvent>

    fun dispose()
}