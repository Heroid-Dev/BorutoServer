package com.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.naming.AuthenticationException

fun Route.root(){
    get("/") {
        call.respond(message = "Welcome to Boruto API!", status = HttpStatusCode.OK)
    }
    get("/hero"){
        call.respond(message = "Hero is Here!", status = HttpStatusCode.OK)
    }
    get("/test2"){
        throw AuthenticationException()
    }
    staticResources("/images", basePackage = "images")
}