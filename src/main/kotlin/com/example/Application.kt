package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureCallLog()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureKoin()
    configureStatusPages()
}
