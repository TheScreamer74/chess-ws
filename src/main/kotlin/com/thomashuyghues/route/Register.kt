package com.thomashuyghues.route

import com.thomashuyghues.database.users.createUser
import com.thomashuyghues.model.endpoint.RegisterRequest
import com.thomashuyghues.utils.hashPassword
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerRoute() {
    post("/register") {
        val registerRequest = call.receive<RegisterRequest>()

        // Hash the password using bcrypt
        val passwordHashGen = hashPassword(registerRequest.password)

        val userRegistered = createUser(registerRequest.username, registerRequest.email, passwordHashGen)

        if (userRegistered != null)
            // Respond with success message
            call.respond(HttpStatusCode.Created, "Welcome ${userRegistered.username}")
        else
            call.respond(HttpStatusCode.InternalServerError, "Error happened during registration")
    }
}