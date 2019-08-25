package com.example.myapplication.DI.Modules

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class CompositeDisposableModule {
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Provides
    fun getCompositDisposable():CompositeDisposable = compositeDisposable
}