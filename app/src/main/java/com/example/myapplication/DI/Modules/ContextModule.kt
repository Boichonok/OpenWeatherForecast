package com.example.myapplication.DI.Modules

import android.content.Context
import dagger.Module
import dagger.Provides



@Module
class ContextModule {

    var context: Context

    constructor(context: Context)
    {
        this.context = context
    }

    @Provides
    fun context(): Context {
        return context.getApplicationContext()
    }

}