package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import javax.naming.AuthenticationException

fun Application.configureStatusPages() {
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                message = "Error 404: Page Not Found!",
                status = HttpStatusCode.NotFound
            )
        }
        exception<AuthenticationException> { call, _ ->
            call.respond(
                status = HttpStatusCode.OK,
                message = "Authentication error"
            )
        }
    }
}