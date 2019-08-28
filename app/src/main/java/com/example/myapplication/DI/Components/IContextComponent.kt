package com.example.myapplication.DI.Components

import android.content.Context
import com.example.myapplication.DI.Modules.ContextModule
import dagger.Component

@Component(modules = [ContextModule::class])
interface IContextComponent {
    fun getContext(): Context
}