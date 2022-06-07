package dev.sancu.controllers

import dev.sancu.models.JsonSupport
import dev.sancu.services.UserService
import dev.sancu.models.CreateOrUpdateUser
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives

class UserController(userService: UserService)(implicit ec: ExecutionContext) extends Directives with JsonSupport {
  val routes: server.Route = {
    concat(
      get {
        path("users") {
          onComplete(userService.get) {
            case Success(users) => complete(StatusCodes.OK, users)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        } ~ path("users" / LongNumber) { id =>
          onComplete(userService.find(id)) {
            case Success(None) => complete(StatusCodes.NotFound)
            case Success(user) => complete(StatusCodes.OK, user)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      },
      post {
        path("users") {
          entity(as[CreateOrUpdateUser]) { createUser =>
            onComplete(userService.create(createUser)) {
              case Success(user) => complete(StatusCodes.Created, user)
              case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
            }
          }
        }
      },
      put {
        path("users" / LongNumber) { id =>
          entity(as[CreateOrUpdateUser]) { updateUser =>
            onComplete(userService.update(id, updateUser)) {
              case Success(user) => complete(StatusCodes.Accepted, user)
              case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
            }
          }
        }
      },
      delete {
        path("users" / LongNumber) { id =>
          onComplete(userService.delete(id)) {
            case Success(_) => complete(StatusCodes.Accepted)
            case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
          }
        }
      }
    )
  }
}
