package com.example.di

import com.example.heroRepository.HeroRepository
import com.example.heroRepository.HeroRepositoryImpl
import org.koin.dsl.module

val KoinModules= module {
    single<HeroRepository>{
        HeroRepositoryImpl()
    }
}