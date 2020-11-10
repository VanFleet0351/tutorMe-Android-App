package com.example.tutorme

import io.reactivex.disposables.Disposable

class ObservableUtils {
    companion object {
        fun safelyDispose(disposable: Disposable?) {
            if (disposable != null && !disposable.isDisposed) {
                disposable.dispose()
            }
        }
    }
}