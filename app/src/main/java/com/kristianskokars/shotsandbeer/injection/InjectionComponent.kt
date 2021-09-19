package com.kristianskokars.shotsandbeer.injection

import com.kristianskokars.shotsandbeer.ui.GameViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [InjectionModule::class])
interface InjectionComponent {
    fun inject(target: GameViewModel)
}
