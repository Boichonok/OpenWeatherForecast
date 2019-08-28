package com.example.myapplication.DI.Components.ActivityComponents

import com.example.myapplication.DI.Modules.ActivityModules.MainActivityModule
import com.example.myapplication.DI.Modules.ContextModule
import com.example.myapplication.DI.ScopeAnnotations.MainActivityScope
import com.example.myapplication.View.MainActivity
import dagger.Component


@Component(modules = [MainActivityModule::class])
@MainActivityScope
interface IMainActivityComponent {
    fun inject(mainActivity: MainActivity)
}